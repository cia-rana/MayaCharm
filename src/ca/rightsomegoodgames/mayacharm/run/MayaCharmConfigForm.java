package ca.rightsomegoodgames.mayacharm.run;

import ca.rightsomegoodgames.mayacharm.run.debug.MayaCharmDebugConfig;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.ui.EditorTextField;

import javax.swing.*;

public class MayaCharmConfigForm {
    private Project project;

    private JPanel rootPanel;
    private JRadioButton fileRadioButton;
    private JRadioButton codeRadioButton;
    private EditorTextField codeField;
    private TextFieldWithBrowseButton fileField;

    public MayaCharmConfigForm(Project project) {
        this.project = project;
        UpdateControls();
        fileRadioButton.addItemListener(e -> UpdateControls());
        codeRadioButton.addItemListener(e -> UpdateControls());
    }

    private void UpdateControls() {
        codeField.setEnabled(codeRadioButton.isSelected());
        fileField.setEnabled(fileRadioButton.isSelected());
    }

    public JComponent getRootPanel() {
        return rootPanel;
    }

    public String getCodeText() {
        return codeField.getText();
    }

    public void setCodeText(String value) {
        codeField.setText(value);
    }

    public String getFilePath() {
        return fileField.getText();
    }

    public void setFilePath(String value) {
        fileField.setText(value);
    }

    public MayaCharmDebugConfig.ExecutionType getExecutionType() {
        if (codeRadioButton.isSelected()) {
            return MayaCharmDebugConfig.ExecutionType.CODE;
        }
        return MayaCharmDebugConfig.ExecutionType.FILE;
    }

    public void setExecutionType(MayaCharmDebugConfig.ExecutionType type) {
        Boolean isCode = type == MayaCharmDebugConfig.ExecutionType.CODE;
        codeRadioButton.setSelected(isCode);
        fileRadioButton.setSelected(!isCode);
    }

    private void createUIComponents() {
        FileChooserDescriptor fileTypeFilter = new FileChooserDescriptor(true, false, false, false, false, false);
        fileField = new TextFieldWithBrowseButton();
        fileField.addBrowseFolderListener("Select File", "Select python file to execute in maya.", project, fileTypeFilter);

        codeField = new EditorTextField("", project, FileTypeManager.getInstance().getFileTypeByExtension(".py"));
        codeField.setOneLineMode(false);
    }
}
