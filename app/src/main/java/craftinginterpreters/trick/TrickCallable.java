package craftinginterpreters.trick;

import java.util.List;

interface TrickCallable {
    Object call(Interpreter interpreter, List<Object> arguements);
}
