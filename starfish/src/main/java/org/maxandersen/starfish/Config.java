package org.maxandersen.starfish;
import io.quarkus.runtime.annotations.RegisterForReflection;
@RegisterForReflection
public class Config {
    public String ide;
    public String clone_path;
    
    public Config(){
    //Default constructor
    }

    public Config(String ide,String clone_path){
    this.ide=ide;
    this.clone_path=clone_path;

    }
   
    public String getIde() {
        return ide;
    }
    public void setIde(String ide) {
        this.ide = ide;
    }
    public String getClone_path() {
        return clone_path;
    }
    public void setClone_path(String clone_path) {
        this.clone_path = clone_path;
    }
  
}