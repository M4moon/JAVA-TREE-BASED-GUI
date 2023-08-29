import com.mycompany.hmmm.ProjectWindow;
import javax.swing.*;
import java.awt.*;

public class Internal {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> createAndShowGUI());
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("Multiple Internal Frames Example");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 600);
        frame.setLayout(new GridLayout(1, 3));

        // Create three internal frames
        JInternalFrame internalFrame1 = createInternalFrame("Tree", Color.RED);
//        JInternalFrame internalFrame2 = createInternalFrame("Group", Color.GREEN);

        // Add internal frames to desktop pane
        JDesktopPane desktopPane = new JDesktopPane();
        desktopPane.add(internalFrame1);
        //desktopPane.add(internalFrame2);

        // Add the desktop pane to the main frame
        frame.add(desktopPane);

        // Show the main frame
        frame.setVisible(true);
    }

    private static JInternalFrame createInternalFrame(String title, Color color) {
        JInternalFrame internalFrame = new JInternalFrame(title, false, false, true, true);
        internalFrame.setSize(984, 400);
        internalFrame.setBackground(color);

        // Add ProjectWindow to the first internal frame only
        if (title.equals("Tree")) {
            ProjectWindow projectWindow = new ProjectWindow();
            internalFrame.add(projectWindow);
        }
  /*      if (title.equals("Group")) {
            // Add Groups to the second internal frame
            Groups groups = new Groups();
            internalFrame.add(groups);
        }
*/
        internalFrame.setVisible(true);

        return internalFrame;
    }
}
