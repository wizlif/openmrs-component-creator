package io.github.wizlif.mets.mets.actions

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.vfs.LocalFileSystem
import java.io.File
import java.io.IOException
import java.nio.file.Files
import kotlin.io.path.Path

fun AnActionEvent.createFiles(createFolder: Boolean = true) {
    val folderPath = dataContext.getData(CommonDataKeys.VIRTUAL_FILE)?.path
    if (folderPath.isNullOrEmpty()) {
        Messages.showErrorDialog("No path", "Error")
        return
    }

    val fileName: String? = Messages.showInputDialog("Enter OpenMRS Component name", "OpenMRS Component", null)

    if (fileName.isNullOrEmpty()) {
        Messages.showErrorDialog("No component name", "Error")
        return
    }

    val fN = fileName.getSnakeCase("-")
    val baseFilePath = Path(path = folderPath).resolve(Path(path = fN))
    val filePath = when {
        createFolder -> baseFilePath.resolve(Path(path = fN))
        else -> baseFilePath
    }.toString()

    // Create the file
    try {
        if (createFolder) {
            Files.createDirectory(baseFilePath)
        }

        val sass = "$filePath.scss"
        val resource = "$filePath.resource.tsx"
        val component = "$filePath.component.tsx"
        val test = "$filePath.test.tsx"
        val className = fileName.getPascalCase()
        File(sass).createNewFile()
        File(resource).createNewFile()
        File(test).createNewFile()
        File(test).writeText(
            """
    import React from "react";
    
    import { render, cleanup } from "@testing-library/react";
    import $className from "./$fN.component";
    
    describe("Test the ${fileName.getSnakeCase(" ")}", () => {
      afterEach(cleanup);
      it(`renders without dying`, () => {
        render(<$className />);
      });
    });
    """.trimIndent()
        )

        File(component).createNewFile()

        File(component).writeText(
            """
                    import React from "react";
                    import { useTranslation } from "react-i18next";
                    import styles from "./${fileName.getSnakeCase("-")}.scss"
    
                    interface ${className}Props {
                        prop?:string
                    }
    
                    const ${className}: React.FC<${className}Props> = () => {
                      const { t } = useTranslation();
    
                      return <></>;
                    };
    
                    export default ${className};
                """.trimIndent()
        )

        LocalFileSystem.getInstance().findFileByPath(folderPath)?.refresh(false, true)
    } catch (ex: IOException) {
        Messages.showErrorDialog("Error creating file: " + ex.message, "Error")
    }
}