package comCARBONCOPY.craftinginterpreters.trick;

import java.util.HashMap;
import java.util.Map;
public class Environment {
    final Environment enclosing;
    private  final Map<String,Object> values = new HashMap<>();

    Environment(){
        enclosing = null;
    }

    Environment(Environment enclosing){
        this.enclosing = enclosing;
    }

    /*Get variable value if found otherwise throws run time error
    * @param: Token object of variable
    * @return: Object value of the found variable
    */
    Object get(Token name){
        if(values.containsKey(name.lexeme)){
            return values.get(name.lexeme);
        }
        if(enclosing != null) return enclosing.get(name);

        throw new RuntimeError(name,"Undefined variable '" + name.lexeme + "'.");
    }
    /*Similar to define a new variable except we cant make new ones only assign existing ones
    * @param: Token name of var, Object the object value of var
    * @return: none*/
    void assign(Token name, Object value){
        if(values.containsKey(name.lexeme)){
            values.put(name.lexeme,value);
            return;
        }
        if(enclosing != null){
            enclosing.assign(name, value);
            return;
        }

        throw new RuntimeError(name,
                "Undefined variable '" + name.lexeme + "'.");
    }

    /*Puts the name and value in the hashmap environment. SEMANTIC CHOICE:
    * this method allows us to define and redefine without errors (i.e var a = 1, then var a = 2 is valid
    * there are different approaches to this, but we will follow Scheme's lexical variable scoping and follow this rule
    * @param: String name of variable, Object value of assigned variable
    * @return: none*/
    void define(String name, Object value){
        values.put(name,value);
    }
}
