package test.springrhino.springrhino.runner.web;


import jdk.nashorn.api.scripting.ScriptUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import test.springrhino.springrhino.runner.data.PreProcessorRepository;
import test.springrhino.springrhino.runner.domain.PreProcessor;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping(path = "/process/", produces = "application/json")
public class PreProcessorController {


    private final PreProcessorRepository repository;

    @Autowired
    public PreProcessorController(PreProcessorRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public Iterable<PreProcessor> list() {
        return repository.findAll();
    }


    @PostMapping
    public PreProcessor addPreProcessor(@Valid @RequestBody PreProcessor preProcessor) {
        return repository.save(preProcessor);
    }

    @PostMapping(path = "/run/{id}")
    public ResponseEntity<Map<String,Object>> run(@PathVariable("id") long id, @RequestBody Map<String, Object> params) {
        Optional<PreProcessor> tmp = repository.findById(id);

        if (!tmp.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        try {
            ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
            Invocable inv = (Invocable) engine;
            engine.eval(tmp.get().getScript());
            List<String> p = (List<String>) params.get("params");
            Object result = inv.invokeFunction("process", String.join("\n", p));
            // convert into java class
            String results = (String) ScriptUtils.convert(result, String.class);
            Map<String, Object> tmpResponse = new HashMap<>();
            tmpResponse.put("result", results.split("\n"));
            return new ResponseEntity<>(tmpResponse, HttpStatus.OK);
        } catch (ScriptException | NoSuchMethodException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }


}
