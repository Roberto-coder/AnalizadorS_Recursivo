import java.util.List;

public class ASDR implements Parser{

    private int i = 0;
    private boolean hayErrores = false;
    private Token preanalisis;
    private final List<Token> tokens;


    public ASDR(List<Token> tokens){
        this.tokens = tokens;
        preanalisis = this.tokens.get(i);
    }

    @Override
    public boolean parse() {
        Q();

        if(preanalisis.tipo == TipoToken.EOF && !hayErrores){
            System.out.println("Consulta correcta");
            return  true;
        }else {
            System.out.println("Se encontraron errores");
        }
        return false;
    }

    // Q -> select D from T
    private void Q(){
        match(TipoToken.SELECT);
        D();
        match(TipoToken.FROM);
        T();
    }

    // D -> distinct P | P
    private void D(){
        if(hayErrores)
            return;

        if(preanalisis.tipo == TipoToken.DISTINCT){
            match(TipoToken.DISTINCT);
            P();
        }
        else if (preanalisis.tipo == TipoToken.ASTERISCO
                || preanalisis.tipo == TipoToken.IDENTIFICADOR) {
            P();
        }
        else{
            hayErrores = true;
            System.out.println("Se esperaba 'distinct' or '*' or 'identificador'");
        }
    }

    // P -> * | A
    private void P(){
        if(hayErrores)
            return;

        if(preanalisis.tipo == TipoToken.ASTERISCO){
            match(TipoToken.ASTERISCO);
        }
        else if(preanalisis.tipo == TipoToken.IDENTIFICADOR){
            A();
        }
        else{
            hayErrores = true;
            System.out.println("Se esperaba '*' or 'identificador'");
        }
    }

    // A -> A2 A1
    private void A(){
        if(hayErrores)
            return;

        A2();
        A1();
    }

    // A2 -> id A3
    private void A2(){
        if(hayErrores)
            return;

        if(preanalisis.tipo == TipoToken.IDENTIFICADOR){
            match(TipoToken.IDENTIFICADOR);
            A3();
        }
        else{
            hayErrores = true;
            System.out.println("Se esperaba un 'identificador'");
        }
    }

    // A1 -> ,A | Ɛ
    private void A1(){
        if(hayErrores)
            return;

        if(preanalisis.tipo == TipoToken.COMA){
            match(TipoToken.COMA);
            A();
        }
    }

    // A3 -> . id | Ɛ
    private void A3(){
        if(hayErrores)
            return;

        if(preanalisis.tipo == TipoToken.PUNTO){
            match(TipoToken.PUNTO);
            match(TipoToken.IDENTIFICADOR);
        }
    }


    /*private void match(TipoToken tt){
        if(preanalisis.tipo == tt){
            i++;
            preanalisis = tokens.get(i);
        }
        else{
            hayErrores = true;
            System.out.println("Error encontrado");
        }

    }*/
    private void match(TipoToken tt) {
        if (hayErrores) return;

        if (preanalisis.tipo == tt) {
            i++;
            if (i < tokens.size()) {
                preanalisis = tokens.get(i);
            } else {
                // Manejo del final de los tokens
                preanalisis = new Token(TipoToken.EOF, "", -1);
            }
        } else {
            hayErrores = true;
            Principal.error(preanalisis.posicion, "Se esperaba " + tt + ", pero se encontró " + preanalisis.tipo);
        }
    }

    // T -> T2 T1
    private void T() {
        if (hayErrores) return;

        T2();
        T1();
    }

    // T2 -> id T3
    private void T2() {
        if (hayErrores) return;

        if (preanalisis.tipo == TipoToken.IDENTIFICADOR) {
            match(TipoToken.IDENTIFICADOR);
            T3();
        } else {
            hayErrores = true;
            System.out.println("Se esperaba un 'identificador'");
        }
    }

    // T1 -> , T | ε
    private void T1() {
        if (hayErrores) return;

        if (preanalisis.tipo == TipoToken.COMA) {
            match(TipoToken.COMA);
            T();
        }
        // Si no hay una coma, se asume la producción epsilon (ε), por lo que no se hace nada.
    }

    // T3 -> id | ε
    private void T3() {
        if (hayErrores) return;

        if (preanalisis.tipo == TipoToken.IDENTIFICADOR) {
            match(TipoToken.IDENTIFICADOR);
        }
        // Si no hay un identificador, se asume la producción epsilon (ε), por lo que no se hace nada.
    }

    }
