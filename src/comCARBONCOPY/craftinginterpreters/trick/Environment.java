package comCARBONCOPY.craftinginterpreters.trick;

import java.util.HashMap;
import java.util.Map;
public class Environment {
    private  final Map<String,Object> values = new HashMap<>();

    /*Puts the name and value in the hashmap environment. SEMANTIC CHOICE:
    * this method allows us to define and redefine without errors (i.e var a = 1, then var a = 2 is valid
    * there are different approaches to this, but we will follow Scheme's lexical variable scoping and follow this rule
    * @param: String name of variable, Object value of assigned variable
    * @return: none*/
    void define(String name, Object value){
        values.put(name,value);
    }
}
