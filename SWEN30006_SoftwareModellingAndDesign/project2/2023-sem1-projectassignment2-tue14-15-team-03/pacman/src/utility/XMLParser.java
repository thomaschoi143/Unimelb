/*
 * Team03
 * Member: Thomas Choi
 *         Lucas Chan
 *         Joshua Wei Han Ch'ng
 */
package src.utility;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import src.mapeditor.editor.Constants;
import src.gameGrid.GridElementType;
import src.mapeditor.grid.GridModel;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class XMLParser {
    // Code was extracted from the Controller class in the mapeditor.editor package of the provided codebase
    public static GridModel readFileToModel(File selectedFile) {
        SAXBuilder builder = new SAXBuilder();
        Document document;
        GridModel gridModel = new GridModel(Constants.mapWidth, Constants.mapHeight, GridElementType.PathTile.toChar());
        try {
            document = (Document) builder.build(selectedFile);
            Element rootNode = document.getRootElement();

            List sizeList = rootNode.getChildren("size");
            Element sizeElem = (Element) sizeList.get(0);
            int height = Integer.parseInt(sizeElem
                    .getChildText("height"));
            int width = Integer
                    .parseInt(sizeElem.getChildText("width"));
            gridModel = new GridModel(width, height, GridElementType.PathTile.toChar());

            List rows = rootNode.getChildren("row");
            for (int y = 0; y < rows.size(); y++) {
                Element cellsElem = (Element) rows.get(y);
                List cells = cellsElem.getChildren("cell");

                for (int x = 0; x < cells.size(); x++) {
                    Element cell = (Element) cells.get(x);
                    String cellValue = cell.getText();

                    char tileNr = GridElementType.valueOf(cellValue).toChar();
                    gridModel.setTile(x, y, tileNr);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return gridModel;
    }

    public static void modelToXML(GridModel model, File file) throws IOException {
        Element level = new Element("level");
        Document doc = new Document(level);
        doc.setRootElement(level);

        Element size = new Element("size");
        int height = model.getHeight();
        int width = model.getWidth();
        size.addContent(new Element("width").setText(width + ""));
        size.addContent(new Element("height").setText(height + ""));
        doc.getRootElement().addContent(size);

        for (int y = 0; y < height; y++) {
            Element row = new Element("row");
            for (int x = 0; x < width; x++) {
                char tileChar = model.getTile(x,y);
                String type = GridElementType.charToGridElementType(tileChar).toString();

                Element e = new Element("cell");
                row.addContent(e.setText(type));
            }
            doc.getRootElement().addContent(row);
        }
        XMLOutputter xmlOutput = new XMLOutputter();
        xmlOutput.setFormat(Format.getPrettyFormat());
        xmlOutput.output(doc, new FileWriter(file));
    }
}
