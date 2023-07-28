# Java-Compiler-DomainSpecific

This is a Java compiler, called **Trick**, built to understand how compilers are built and with added functionality we deemed would improve the language.

Programming Language Developed: Trick - a domain specific version of Java

## Credits

Please note this language is built upon following the following source very closesly and the credit for the basics functionality and design of the compiler goes to this source:

[Crafting Interpreters](https://craftinginterpreters.com/)

## Trick Language Documentation:

        While most of the language follows conventional Java implementation there are notable differences

###      Trick type	Java representation
####    Any Lox value: ||||||||||||||||   Object:
        nil	                null
        Boolean	        Boolean
        number	        Double
        string	        String
        char            Character

###      Truth?
        Any boolean false and nil value are considered false while all else is true

###      String/Char Manipulation:

        Arithemtic a + b also works to concatenate string and chars with any other string,char, or number to create a final string

###     Numbers/Arithmetic
        All numbers are doubles
        Supports checking for equality/inequality 
                (=/!=) between any types - i.e check for 3 = three (Reals on ##Truth? logic)

###     Exit Codes
        65: Syntax error while scanning
        70: Computation error while running the interpreter 

HEAD NOTES FOR DEVELOPERS:
- Any mention of "|*********|" indicates an area of improvement or advancement  needed for development
- Any mention of "|&&&&&&&&&| indicates uncertainties or additions needed for basic functionality
-Any mention of ?!?!?!?!?! indicates a new addition that needs to be tested

IDEAS FOR IMPLEMENTATION:
- To quote Crafting Interpreters "Ideally, we would have an actual abstraction, some kind of “ErrorReporter” interface that gets passed to the scanner and parser so that we can swap out different reporting strategies." - may be an opporutnity for AI to play a role


## Planning


Chapter - 5 by 7th June (Ratiq)
Chapter - 4 by 7th June (Youssef) - DONE
Extra Functionality - Finish by June 14th
1. Tokenize Chars - DONE
2. Unknown characters blob should be reported as one entity rather than individual i.e "これ は　だいじょぶない" - DONE
3. Specific error reporting


## Progress

Chapter 4 Complete
Char Tokenization Complete - not formally tested


## Documentation

NUMBER LITERALS:
when performing method calls on numbers, negation will not take precendence over the method
i.e: print -123.abs(); --> -123

## FORMALLY UNTESTED SECTIONS

        -Char tokenization
        -Blob Reporting