package rafael.afl.parser;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import rafael.afl.parser.ArvoreBinaria.Node;

/**
 * Analisador de fórmulas. As fórmulas são compostas por letras proposicionais {A-Z0-9}, conectivos {&, |, ->, <-> e ~}
 * e parênteses.
 */
public class AnalisadorFormula {

	public enum Conectivo {
		BICONDICIONAL("Bicondicional", "<->"), CONDICIONAL("Condicional", "->"), CONJUNCAO("Conjunção", "&"), DISJUNCAO("Disjunção", "|"), NEGACAO("Negação", "~");

		/**
		 * Procura pelo conectivo a partir de seu alias
		 * @param alias
		 * @return
		 */
		public static Conectivo getConectivoPorAlias(String alias) {
			for (Conectivo conectivo : Conectivo.values()) {
				if (alias.equalsIgnoreCase(conectivo.alias)) {
					return conectivo;
				}
			}
			return null;
		}

		// Representacao adotada no aplicativo do conectivo:
		public final String alias;

		public final String nome;

		private Conectivo(String nome, String alias) {
			this.nome = nome;
			this.alias = alias;
		}
	}

	private static final String EXCEPTION_FORMULA_INVALIDA = "A fórmula de entrada não é uma fórmula bem formada.";
	private static final String EXCEPTION_SIMBOLO_INVALIDO = "Símbolo inválido";

	/**
	 * Valida uma fórmula e, se for uma fórmula bem formada, cria sua árvore de decomposição.
	 * @param formula
	 * @return Árvore de decomposição da fórmula
	 * @throws ParseException - Caso a fórmula não seja uma fórmula bem formada é lançada esta exceção
	 */
	public static ArvoreBinaria<String> criarArvoreDecomposicao(String formula) throws ParseException {
		ArvoreBinaria<String> arvore = new ArvoreBinaria<String>();
		try {
			AnalisadorFormula.criarArvoreDecomposicao(AnalisadorFormula.separarFormula(formula.toUpperCase()), arvore, null, null);
		} catch (ParseException ex) {
			throw ex;
		} catch (Exception ex) {
			throw new ParseException(AnalisadorFormula.EXCEPTION_FORMULA_INVALIDA, 0);
		}
		return arvore;
	}

	private static void criarArvoreDecomposicao(String[] formula, ArvoreBinaria<String> arvore, ArvoreBinaria.Node<String> nodePai, ArvoreBinaria.Lado lado) throws Exception {
		// Caso base: a expressão é uma letra proposicional
		if (formula.length == 1 && formula[0].matches("[A-Z]+[0-9]*")) {
			arvore.adicionar(formula[0], nodePai, lado);
			return;
		}

		// Se não é o caso base, então a fórmula possui sub-fórmula com conectivo(s) e parêntese(s):
		if (formula.length < 4 || !formula[0].equals("(") || !formula[formula.length - 1].equals(")")) {
			throw new ParseException(AnalisadorFormula.EXCEPTION_FORMULA_INVALIDA, 0);
		}

		if (formula[1].equals("~")) {
			Node<String> novoNodeConectivo = arvore.adicionar(formula[1], nodePai, lado);
			List<String> formulaAsList = Arrays.asList(formula);
			// Cria a árvore de decomposição do filho da Negação:
			AnalisadorFormula.criarArvoreDecomposicao(formulaAsList.subList(2, formula.length - 1).toArray(new String[0]), arvore, novoNodeConectivo, ArvoreBinaria.Lado.ESQUERDA);
			return;
		} else {
			int indiceConectivo = -1;
			int contadorparêntesesAbertos = 0;
			for (int i = 1; i < formula.length - 1; i++) {
				if (formula[i].equals("(")) {
					// Incrementa o número de parênteses abertos
					contadorparêntesesAbertos++;
				} else if (formula[i].equals(")")) {
					// Decrementa o número de parênteses abertos, pois foram fechados:
					contadorparêntesesAbertos--;
				} else if (contadorparêntesesAbertos == 0 && formula[i].matches("(\\|)|(&)|(<->)|(->)")) {
					// Guarda o índice do conectivo na expressão:
					indiceConectivo = i;
					break;
				}
			}
			ArvoreBinaria.Node<String> novoNodeConectivo = arvore.adicionar(formula[indiceConectivo], nodePai, lado);
			List<String> formulaAsList = Arrays.asList(formula);

			String[] formulaEsquerda = formulaAsList.subList(1, indiceConectivo).toArray(new String[0]);
			String[] formulaDireita = formulaAsList.subList(indiceConectivo + 1, formula.length - 1).toArray(new String[0]);

			// Cria as árvores esquerda e direita do conectivo:
			AnalisadorFormula.criarArvoreDecomposicao(formulaEsquerda, arvore, novoNodeConectivo, ArvoreBinaria.Lado.ESQUERDA);
			AnalisadorFormula.criarArvoreDecomposicao(formulaDireita, arvore, novoNodeConectivo, ArvoreBinaria.Lado.DIREITA);
			return;
		}
	}

	/**
	 * Valida e divide a expressão por conectivos, letras proposicionais e parênteses
	 * @param formula
	 * @return
	 * @throws ParseException
	 */
	public static String[] separarFormula(String formula) throws ParseException {
		// Cria a Expressão regular para validar e separar os símbolos aceitos:
		Pattern patternRegExp = Pattern.compile("([()]{1})|(~)|(\\|)|(&)|(<->)|(->)|([A-Z]+[0-9]*)|( )*");
		Matcher matcher = patternRegExp.matcher(formula);
		List<String> listaSimbolos = new ArrayList<String>();
		int index = 0;

		while (matcher.find()) {
			String simboloAnterior = formula.subSequence(index, matcher.start()).toString();
			String simboloAtual = formula.subSequence(matcher.start(), matcher.end()).toString();

			// Valida os símbolos:
			if (!patternRegExp.matcher(simboloAnterior).matches()) {
				throw new ParseException(AnalisadorFormula.EXCEPTION_SIMBOLO_INVALIDO + ": " + simboloAnterior, index);
			} else if (!patternRegExp.matcher(simboloAtual).matches()) {
				throw new ParseException(AnalisadorFormula.EXCEPTION_SIMBOLO_INVALIDO + ": " + simboloAtual, index);
			}
			// Verifica se não é espaço e adiciona:
			if (!simboloAnterior.trim().isEmpty()) {
				listaSimbolos.add(simboloAnterior);
			}
			if (!simboloAtual.trim().isEmpty()) {
				listaSimbolos.add(simboloAtual);
			}
			// Avança para o índice do fim do operador:
			index = matcher.end();
		}

		// Adiciona o restante da expressão:
		String restante = formula.subSequence(index, formula.length()).toString();
		if (!restante.isEmpty()) {
			listaSimbolos.add(formula.subSequence(index, formula.length()).toString());
		}

		// Valida todos os símbolos:
		for (String string : listaSimbolos) {
			if (!patternRegExp.matcher(string).matches()) {
				throw new ParseException(AnalisadorFormula.EXCEPTION_SIMBOLO_INVALIDO, index);
			}
		}

		return listaSimbolos.toArray(new String[listaSimbolos.size()]);
	}

	private AnalisadorFormula() {
	}
}
