package io.github.wizlif.mets.mets.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.ui.Messages
import java.io.File
import java.io.IOException
import java.nio.file.Files
import kotlin.io.path.Path


class CreateComponent : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val folderPath = e.dataContext.getData(CommonDataKeys.VIRTUAL_FILE)?.path
        if (folderPath.isNullOrEmpty()) {
            Messages.showErrorDialog("No path", "Error")
            return
        }

        val fileName: String? = Messages.showInputDialog("Enter OpenMRS Component name", "OpenMRS Component", null)

        if (fileName.isNullOrEmpty()) {
            Messages.showErrorDialog("No component name", "Error")
            return
        }


        // Construct the file path
//        val filePath = "$/$fileName"
        val fN = fileName.getSnakeCase("-")
        val baseFilePath = Path(path = folderPath).resolve(Path(path = fN))
        val filePath = baseFilePath.resolve(Path(path = fN)).toString()

        // Create the file
        try {
            val sass = "$filePath.scss"
            val resource = "$filePath.resource.tsx"
            val component = "$filePath.component.tsx"
            val test = "$filePath.test.tsx"
            Files.createDirectory(baseFilePath)

            File(sass).createNewFile()
            File(resource).createNewFile()
            File(test).createNewFile()
            File(test).writeText("""
                jest.mock('@openmrs/esm-framework', () => {
                  const originalModule = jest.requireActual('@openmrs/esm-framework');
                  return {
                    ...originalModule,
                    openmrsFetch: jest.fn(),
                  };
                });
            """.trimIndent())

            File(component).createNewFile()
            val className = fileName.getPascalCase()
            File(component).writeText("""
                import React from 'react';
                import { useTranslation } from 'react-i18next';
                import { useLocations } from '@openmrs/esm-framework';

                interface ${className}Props {}

                const ${className}Component: React.FC<${className}Props> = ({}) => {
                  const { t } = useTranslation();

                  const locations = useLocations();

                  return <></>;
                };

                export default ${className}Component;
            """.trimIndent())

            e.getData(CommonDataKeys.VIRTUAL_FILE)?.refresh(false,true)
        } catch (ex: IOException) {
            Messages.showErrorDialog("Error creating file: " + ex.message, "Error")
        }
    }
}

