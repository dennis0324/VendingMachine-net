import {decode} from 'ini';
import fs from 'fs';
class INIConfig{
    data = {}
    path = ''
    load(path){
        this.path = path;
        if(fs.existsSync(path)){
            this.data = decode(fs.readFileSyncz("./"))
        }
    }
    save(){
        fs.writeFileSync(this.path,this.data)
    }
}