package rafael.afl.core;

public interface Constantes {
	String APLICACAO_INFORMACOES = "Este aplicativo foi desenvolvido por Rafael Sales sob orientação do Prof. Marcelino Pequeno";
	String APLICACAO_TITULO = "Analisador de fórmulas lógicas bem formadas - Rafael Sales";
	String APLICACAO_TITULO_ERRO = "Erro";
	String APLICACAO_TITULO_INFORMACAO = "Informação";

	String FORMULA_CONECTIVOS = "Conectivos: & (Conjunção), | (Disjunção), -> (Condicional), <-> (Bicondicional) e ~ (Negação - unário)";
	String FORMULA_EXEMPLO = "((((~A)->B)|((B<->C)&A))->((A|B)|C))";
	String FORMULA_NOME = "Fórmula";
	String FORMULA_PONTUACAO = "Pontuação: ( e ) (Parênteses)";
	String FORMULA_PROPOSICOES_ACEITAS = "Proposições aceitas: Palavras contendo letras de A-Z";
	String FORMULA_TEXTO_EXEMPLO = "Exemplo de fórmula bem formada: ((((~A)->B)|((B<->C)&A))->((A|B)|C))";

	Object MSG_ERRO_INESPERADO = "Ocorreu um erro inesperado";
	String MSG_FORMULA_BEM_FORMADA = "A fórmula digitada é uma fórmula bem formada!";

}
