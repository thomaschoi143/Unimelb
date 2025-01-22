/* FileManager.java
   Author: Thomas Choi 1202247 */

package server;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.fasterxml.jackson.databind.ser.BeanSerializerFactory;
import shared.whiteboard.drawElements.*;
import shared.whiteboard.drawElements.Rectangle;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class FileManager {
    private String filepath;
    private ObjectMapper mapper;
    private JFileChooser fileChooser;
    private Server server;

    public FileManager(Server server) {
        this.server = server;

        this.mapper = new ObjectMapper();
        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        SimpleModule module = new SimpleModule();
        module.addSerializer(Color.class, new ColorSerializer());
        module.addSerializer(DrawElement.class, new DrawElementSerializer());
        module.addDeserializer(DrawElement.class, new DrawElementDeserializer());
        mapper.registerModule(module);

        fileChooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                "JSON", "json");
        fileChooser.setFileFilter(filter);
        fileChooser.setCurrentDirectory(new java.io.File("."));
    }

    public void newWhiteBoard() {
        filepath = null;
        server.newWhiteBoard(new ArrayList<DrawElement>());
        server.getGui().getStatusInput().setText("Created a new white board");
    }

    public void open() {
        int returnVal = fileChooser.showOpenDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            filepath = fileChooser.getSelectedFile().getName();
            try {
                ArrayList<DrawElement> drawElements = mapper.readValue(new File(filepath), new TypeReference<ArrayList<DrawElement>>() {});
                server.newWhiteBoard(drawElements);
                server.getGui().getStatusInput().setText("Opened a white board");
            } catch (IOException e) {
                server.getGui().getStatusInput().setText("Error when reading file");
                System.out.println("Error when reading file: " + e.getMessage());
            }
        }
    }

    public void save(ArrayList<DrawElement> drawElements) {
        if (filepath == null) {
            saveAs(drawElements);
        } else {
            try {
                mapper.writeValue(new File(filepath), drawElements);
                server.getGui().getStatusInput().setText("Saved the white board to " + new File(filepath).getName());
            } catch (IOException e) {
                server.getGui().getStatusInput().setText("Error when saving the white board");
                System.out.println("Error when saving the white board: " + e.getMessage());
            }
        }
    }

    public void saveAs(ArrayList<DrawElement> drawElements) {
        fileChooser.setSelectedFile(new File("whiteBoard.json"));
        int returnVal = fileChooser.showSaveDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            filepath = fileChooser.getSelectedFile().getPath();
            save(drawElements);
        }
    }

    public static class ColorSerializer extends JsonSerializer<Color> {
        @Override
        public void serialize(Color value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            gen.writeStartObject();
            gen.writeFieldName("argb");
            gen.writeString(Integer.toHexString(value.getRGB()));
            gen.writeEndObject();
        }
    }

    public static class DrawElementSerializer extends JsonSerializer<DrawElement> {
        @Override
        public void serialize(DrawElement value, JsonGenerator gen, SerializerProvider provider) throws IOException {
            gen.writeStartObject();
            JavaType javaType = provider.constructType(value.getClass());
            BeanDescription beanDesc = provider.getConfig().introspect(javaType);
            JsonSerializer<Object> serializer = BeanSerializerFactory.instance.findBeanSerializer(provider,
                    javaType,
                    beanDesc);
            serializer.unwrappingSerializer(null).serialize(value, gen, provider);

            gen.writeObjectField("type", value.getClass().getSimpleName());
            gen.writeEndObject();
        }
    }

    public static class DrawElementDeserializer extends JsonDeserializer<DrawElement> {
        @Override
        public DrawElement deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            TreeNode root = p.getCodec().readTree(p);

            TextNode type = (TextNode) root.get("type");
            ObjectNode colorNode = (ObjectNode) root.get("color");
            Color color = new Color(Integer.parseUnsignedInt(colorNode.get("argb").textValue(), 16), true);
            ObjectNode topLeftNode;
            IntNode diameterX;
            IntNode diameterY;
            ObjectNode centerNode;

            switch(type.textValue()) {
                case "Line":
                    ObjectNode pt1Node = (ObjectNode) root.get("p1");
                    ObjectNode pt2Node = (ObjectNode) root.get("p2");
                    return new Line(color, getMyPoint(pt1Node), getMyPoint(pt2Node));
                case "Circle":
                    topLeftNode = (ObjectNode) root.get("topLeft");
                    diameterX = (IntNode) root.get("diameterX");
                    return new Circle(color, getMyPoint(topLeftNode), diameterX.intValue());
                case "EraserSmall":
                    centerNode = (ObjectNode) root.get("center");
                    return new EraserSmall(getMyPoint(centerNode));
                case "EraserMedium":
                    centerNode = (ObjectNode) root.get("center");
                    return new EraserMedium(getMyPoint(centerNode));
                case "EraserLarge":
                    centerNode = (ObjectNode) root.get("center");
                    return new EraserLarge(getMyPoint(centerNode));
                case "Oval":
                    topLeftNode = (ObjectNode) root.get("topLeft");
                    diameterX = (IntNode) root.get("diameterX");
                    diameterY = (IntNode) root.get("diameterY");
                    return new Oval(color, getMyPoint(topLeftNode), diameterX.intValue(), diameterY.intValue());
                case "Rectangle":
                    topLeftNode = (ObjectNode) root.get("topLeft");
                    IntNode width = (IntNode) root.get("width");
                    IntNode height = (IntNode) root.get("height");
                    return new Rectangle(color, getMyPoint(topLeftNode), width.intValue(), height.intValue());
                case "Text":
                    centerNode = (ObjectNode) root.get("center");
                    TextNode text = (TextNode) root.get("text");
                    return new Text(color, getMyPoint(centerNode), text.textValue());
            }
            return null;
        }

        private MyPoint getMyPoint(ObjectNode node) {
            return new MyPoint(node.get("x").intValue(), node.get("y").intValue());
        }
    }
}
