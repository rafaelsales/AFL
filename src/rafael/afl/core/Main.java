package rafael.afl.core;

import java.text.ParseException;
import java.util.Scanner;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import rafael.afl.parser.AnalisadorFormula;
import rafael.afl.parser.ArvoreBinaria;
import rafael.afl.view.MainWindow;
import rafael.afl.view.ViewUtil;

public class Main {

	public static final String URL_BASE_RESOURCE = "/rafael/afl/resource/";

	private static void iniciarModoConsole() {
		System.out.println("\t--- " + Constantes.APLICACAO_TITULO + " ---");
		Scanner scannerString = new Scanner(System.in);
		System.out.println("- " + Constantes.FORMULA_PROPOSICOES_ACEITAS);
		System.out.println("- " + Constantes.FORMULA_CONECTIVOS);
		System.out.println("- " + Constantes.FORMULA_PONTUACAO);
		System.out.println("- " + Constantes.FORMULA_TEXTO_EXEMPLO);
		System.out.print("Digite a fórmula a ser analisada: ");
		String formula = scannerString.nextLine();
		System.out.println();

		try {
			ArvoreBinaria<String> arvoreDecomposicao = AnalisadorFormula.criarArvoreDecomposicao(formula);
			System.out.println(Constantes.MSG_FORMULA_BEM_FORMADA);
			System.out.println("Árvore de decomposição da fórmula:");
			arvoreDecomposicao.exibir(System.out);
		} catch (ParseException ex) {
			System.out.println(ex.getMessage());
		}
	}

	private static void iniciarModoSwing() {
		// Define o Look And Feel para o padrão do Sistema Operacional:
		ViewUtil.setDefaultLookAndFeel();
		// Inicializa a janela principal da aplicação na thread de eventos do AWT:
		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				public void run() {
					MainWindow principal = new MainWindow();
					principal.setVisible(true);
				}
			});
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(null, Constantes.MSG_ERRO_INESPERADO, Constantes.APLICACAO_TITULO_ERRO, JOptionPane.ERROR_MESSAGE);
			ex.printStackTrace();
		}
	}

	public static void main(String[] args) {
		boolean modoSwing = true;
		if (args.length > 0) {
			if (args[0].equalsIgnoreCase("-c")) {
				modoSwing = false;
			} else {
				if (!args[0].equalsIgnoreCase("--help")) {
					System.out.println("Parâmetro inválido!");
				}
				System.out.println("Parâmetros aceitos:");
				System.out.println("\t--help:\t Exibe este texto de ajuda.");
				System.out.println("\t-c:\t Inicia o programa em modo console. O padrão é em janelas (Swing).");
				return;
			}
		}

		if (modoSwing) {
			Main.iniciarModoSwing();
		} else {
			Main.iniciarModoConsole();
		}
	}
}
