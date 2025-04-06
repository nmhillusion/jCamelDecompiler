package tech.nmhillusion.jCamelDecompilerApp.gui.component;

import javax.swing.*;
import javax.swing.text.html.HTMLEditorKit;
import java.awt.*;

/**
 * created by: nmhillusion
 * <p>
 * created date: 2025-04-06
 */
public class ExplainHowToFilterPane extends JPanel {

    public ExplainHowToFilterPane() {
        this.setLayout(new CardLayout());
        this.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        this.setBackground(Color.WHITE);

        setComponents();
    }

    private void setComponents() {
        final JEditorPane editorPane = new JEditorPane();
        editorPane.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, true);

        final HTMLEditorKit editorKit = new HTMLEditorKit();
        editorPane.setEditorKit(editorKit);
        editorPane.setContentType("text/html");
        editorPane.setText("""
                <html><body>
                Text file contains list of paths to filter, separated by new line.<br>
                It should be relative paths from the target folder path.<br>
                Example:<br>
                <ul>
                    <li>Target folder: <b><i>C:/my-folder</i/></b></li>
                    <li>Class file to filter: <b><i>C:/my-folder/com/example/myapp/Application.class</i/></b></li>
                    <li>Filter path should be: <b><i>com/example/myapp/Application.class</i/></b> <u>or</u> <b><i>com/example/myapp/Application.java</i/></b></li>
                </ul>
                <br>
                App will loop through all class files in the target folder and only decompile on class file what is in the list.
                </body></html>
                """);

        editorPane.setFont(
                new Font(Font.MONOSPACED, Font.PLAIN, 12)
        );

        editorPane.setBackground(Color.WHITE);
        editorPane.setEditable(false);
        editorPane.setOpaque(true);

        this.add(editorPane);
    }

}
