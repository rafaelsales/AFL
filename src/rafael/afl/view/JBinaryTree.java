package rafael.afl.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.image.VolatileImage;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

import rafael.afl.parser.AnalisadorFormula.Conectivo;

@SuppressWarnings("unchecked")
public class JBinaryTree extends JPanel {

	public static abstract class BinaryTreeModel<Model> {

		protected JBinaryTree binaryTree;
		protected Model raiz;

		public BinaryTreeModel() {
		}

		public BinaryTreeModel(Model raiz) {
			this.raiz = raiz;
		}

		public void limpar() {
			raiz = null;
		}

		public abstract int obterAltura();

		public abstract Model obterFilhoDireita(Model modelPai);

		public abstract Model obterFilhoEsquerda(Model modelPai);

		public abstract String obterValor(Model model);

		public void setRaiz(Model raiz) {
			this.raiz = raiz;
			binaryTree.atualizar();
		}
	}

	private static final long serialVersionUID = 1L;

	private Graphics2D dbGraphics;
	private Dimension margem;
	private BinaryTreeModel model;
	private Dimension tamanhoNode;
	private VolatileImage volatileImage;

	public JBinaryTree() {
		setBackground(Color.WHITE);
		tamanhoNode = new Dimension(25, 25);
		margem = new Dimension(2, 2);
	}

	public void atualizar() {
		if (volatileImage == null) {
			// Cria a image utilizada no DoubleBuffering:
			Rectangle maximumWindowBounds = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
			volatileImage = createVolatileImage((int) maximumWindowBounds.getWidth(), (int) maximumWindowBounds.getHeight());
			if (volatileImage == null) {
				return;
			}
			dbGraphics = (Graphics2D) volatileImage.getGraphics();
			dbGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			dbGraphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
			dbGraphics.setBackground(Color.WHITE);
			dbGraphics.setColor(Color.BLACK);
			dbGraphics.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 16));
		}
		dbGraphics.clearRect(0, 0, volatileImage.getWidth(), volatileImage.getHeight());
		if (model == null) {
		} else {
			int altura = model.obterAltura();
			int numColunas = (int) (Math.pow(2, altura) - 1);
			setMinimumSize(new Dimension(numColunas * (margem.width + tamanhoNode.width), altura * (margem.height + tamanhoNode.height)));
			criarTree(model.raiz, 0, 0, numColunas - 1, dbGraphics);
		}
		render(getGraphics());
	}

	private void criarTree(Object node, int linha, int colunaInicio, int colunaFim, Graphics2D graphics) {
		if (node == null) {
			return;
		}
		int colunaNode = (int) Math.floor((colunaFim + colunaInicio) / 2.0);
		int x = colunaNode * (margem.width + tamanhoNode.width);
		int y = linha * (margem.height + tamanhoNode.height);

		graphics.drawOval(x, y, tamanhoNode.width, tamanhoNode.height);
		Conectivo conectivo = Conectivo.getConectivoPorAlias(model.obterValor(node));
		if (conectivo != null) {
			ImageIcon iconeConectivo = ViewUtil.getIconeConectivo(conectivo);
			if (iconeConectivo != null) {
				graphics.drawImage(iconeConectivo.getImage(), x, y, null);
			}
		} else {
			graphics.drawString(model.obterValor(node), x, y);
		}

		if (model.obterFilhoEsquerda(node) != null) {
			criarTree(model.obterFilhoEsquerda(node), linha + 1, colunaInicio, (int) Math.floor((colunaFim - colunaInicio) / 2f) - 1, graphics);
		}
		if (model.obterFilhoDireita(node) != null) {
			criarTree(model.obterFilhoDireita(node), linha + 1, (int) Math.floor((colunaFim - colunaInicio) / 2f) + 1, colunaFim, graphics);
		}
	}

	@Override
	protected void paintComponent(Graphics graphics) {
		super.paintComponent(graphics);
		render(graphics);
	}

	private void render(Graphics graphics) {
		if (graphics == null) {
			return;
		}
		if (volatileImage != null) {
			Toolkit.getDefaultToolkit().sync();
			graphics.drawImage(volatileImage, 0, 0, null);
		}
	}

	public void setModel(BinaryTreeModel binaryTreeModel) {
		model = binaryTreeModel;
		if (binaryTreeModel != null) {
			binaryTreeModel.binaryTree = this;
		}
		atualizar();
	}
}
