package rafael.afl.view;

import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.UIManager;

/**
 * Applet principal da aplicação. Utiliza o mesmo panel principal que a versão em Swing.
 */
public class MainApplet extends Applet {

	private static final long serialVersionUID = 1L;

	private MainPanel mainPanel;

	public MainApplet() {
		try {
			// Define o Look And Feel para o padrão do Sistema Operacional
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception ex) {
		}
	}

	@Override
	public void init() {
		super.init();
		// Define o tamanho da janela:
		setMinimumSize(new Dimension(320, 240));
		setPreferredSize(new Dimension(640, 480));

		// Define o layout para que o panel interno ocupe toda a janela:
		setLayout(new BorderLayout());

		// Adiciona o Panel principal:
		mainPanel = new MainPanel();
		add(mainPanel);
		doLayout();
	}
}
