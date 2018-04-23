DROP TABLE pre_processor;
CREATE TABLE IF NOT EXISTS pre_processor (
  id           BIGINT PRIMARY KEY AUTO_INCREMENT,
  script       VARCHAR(1024) NOT NULL,
  modified TIMESTAMP  DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);


INSERT INTO pre_processor(script) VALUES ('
// This function will take a multiline string and append ''OK'' to each line
// @return the new multiline string
function process(rows){

 var tmp=rows.split(''\n'');

    for(var i=0; i<tmp.length; i++){
            tmp[i] = tmp[i]+":OK";
    }

    return  tmp.join(''\n'');
}
')