package rafael.afl.parser;

import java.io.PrintStream;
import java.io.Serializable;

/**
 * Implemtanção de Árvore Binária
 * @param <V> - Tipo do valor da árvore.
 */
public class ArvoreBinaria<V> implements Serializable {

	public enum Lado {
		DIREITA, ESQUERDA
	}

	/**
	 * Nó da árvore binária
	 * @param <V>
	 */
	public static class Node<V> {

		public Node<V> direita;
		public Node<V> esquerda;
		public V valor;

		private Node() {
		}

		@Override
		public String toString() {
			return valor.toString() == null ? "" : valor.toString();
		}
	}

	private static final long serialVersionUID = 1L;

	/**
	 * Calcula a altura de uma árvore considerando o nodePai como raiz
	 * @param nodePai
	 * @return
	 */
	public static int calcularAltura(Node<?> nodePai) {
		if (nodePai == null) {
			return 0;
		}
		int alturaEsquerda = 0;
		int alturaDireita = 0;
		if (nodePai.esquerda != null) {
			alturaEsquerda = ArvoreBinaria.calcularAltura(nodePai.esquerda);
		}
		if (nodePai.direita != null) {
			alturaDireita = ArvoreBinaria.calcularAltura(nodePai.direita);
		}
		return 1 + Math.max(alturaEsquerda, alturaDireita);
	}

	// Raíz da árvore binária:
	public Node<V> raiz;

	/**
	 * Adiciona um novo valor na árvore como filho no lado <code>lado</code> do nó <code>nodePai</code>
	 * @param valor
	 * @param nodePai
	 * @param lado
	 * @return
	 */
	public Node<V> adicionar(V valor, Node<V> nodePai, Lado lado) {
		if (valor == null || raiz != null && lado == null) {
			throw new IllegalArgumentException("O valor e o lado não podem ser null");
		}
		Node<V> novoNode = new Node<V>();
		novoNode.valor = valor;

		if (raiz == null) {
			raiz = novoNode;
		} else if (nodePai == null) {
			if (lado == Lado.ESQUERDA) {
				novoNode.esquerda = raiz;
			} else {
				novoNode.direita = raiz;
			}
			raiz = novoNode;
		} else {
			if (lado == Lado.ESQUERDA) {
				if (nodePai.esquerda != null) {
					novoNode.esquerda = nodePai.esquerda;
					nodePai.esquerda = novoNode;
				} else {
					nodePai.esquerda = novoNode;
				}
			} else if (lado == Lado.DIREITA) {
				if (nodePai.direita != null) {
					novoNode.direita = nodePai.direita;
					nodePai.direita = novoNode;
				} else {
					nodePai.direita = novoNode;
				}
			}
		}

		return novoNode;
	}

	/**
	 * Calcula a altura da árvore
	 * @return
	 */
	public int calcularAltura() {
		return ArvoreBinaria.calcularAltura(this.raiz);
	}

	/**
	 * Imprime a árvore em horizontalmente para o <code>printStream</code> exibindo os valores dos nós através da
	 * implementação do <code>toString()</code> do tipo do valor <code><V></code> da árvore
	 * @param printStream
	 */
	public void exibir(PrintStream printStream) {
		exibir(printStream, raiz, 1);
	}

	private void exibir(PrintStream printStream, Node<V> node, int nivel) {
		if (node == null) {
			return;
		}
		String identacao = "";
		for (int i = 0; i < nivel; i++) {
			identacao += "|";
		}
		identacao += " ";

		printStream.println(identacao + node.valor.toString());

		if (node.esquerda != null) {
			exibir(printStream, node.esquerda, nivel + 1);
		}
		if (node.direita != null) {
			exibir(printStream, node.direita, nivel + 1);
		}
	}
}
