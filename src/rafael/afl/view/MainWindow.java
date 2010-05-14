package rafael.afl.view;

import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JPanel;

import rafael.afl.core.Constantes;

/**
 * Janela principal da aplicação quando executada em modo Swing standalone
 */
public class MainWindow extends JFrame {

	private static final long serialVersionUID = 1L;

	private JPanel mainPanel;

	public MainWindow() {
		super(Constantes.APLICACAO_TITULO);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// Define o tamanho da janela:
		setMinimumSize(new Dimension(320, 240));
		setPreferredSize(new Dimension(640, 480));
		// Posiciona no centro da tela:
		setLocationRelativeTo(null);

		// Define o Panel principal:
		mainPanel = new MainPanel();
		setContentPane(mainPanel);

		pack();
	}
}