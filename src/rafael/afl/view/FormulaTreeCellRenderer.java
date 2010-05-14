package rafael.afl.view;

import java.awt.Component;

import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

import rafael.afl.parser.AnalisadorFormula.Conectivo;

/**
 * Render que troca os símbolos de fórmulas lógicas adotadas no aplicativo por imagens correspondentes dos símbolos.
 */
public class FormulaTreeCellRenderer extends DefaultTreeCellRenderer {

	private static final long serialVersionUID = 1L;

	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
		super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
		String nodeString = (String) ((DefaultMutableTreeNode) value).getUserObject();

		Conectivo nodeConectivo = Conectivo.getConectivoPorAlias(nodeString);
		// Caso seja um conectivo, define o icone no nó:
		if (nodeConectivo != null) {
			ImageIcon iconeConectivo = ViewUtil.getIconeConectivo(nodeConectivo);
			setToolTipText(nodeConectivo.nome + "; Notação adotada: " + nodeConectivo.alias);
			if (iconeConectivo != null) {
				setIcon(iconeConectivo);
				// Retira o texto para não ficar ambiguo:
				setText("");
			}
		}
		return this;
	}
}
