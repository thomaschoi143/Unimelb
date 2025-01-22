/* WhiteBoard.java
   Author: Thomas Choi 1202247 */

package shared.whiteboard;

import shared.host.Host;
import shared.whiteboard.drawElements.*;
import shared.whiteboard.drawElements.Rectangle;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;

public class WhiteBoard extends JPanel {
    private ActionCode action = ActionCode.FREE;
    private Color color = Color.BLACK;
    private ArrayList<DrawElement> drawElements = new ArrayList<>();
    private Pointer pointer = new Pointer(Color.BLACK, new MyPoint(0, 0));
    private DrawElement localTempDrawElement = null;
    private MyPoint pt1 = new MyPoint(0, 0);
    private MyPoint pt2 = new MyPoint(0, 0);

    public static final HashMap<String, Color> COLORS = new HashMap<>() {{
        put("Black", new Color(0, 0, 0));
        put("Aqua", new Color(0, 255, 255));
        put("Blue", new Color(0, 0, 255));
        put("Fuchsia", new Color(255, 0, 255));
        put("Gray", new Color(153, 153, 153));
        put("Green", new Color(0, 128, 0));
        put("Lime", new Color(0, 255, 0));
        put("Maroon", new Color(128, 0, 0));
        put("Navy", new Color(0, 0, 128));
        put("Olive", new Color(128, 128, 0));
        put("Purple", new Color(128, 0, 128));
        put("Red", new Color(255, 0, 0));
        put("Silver", new Color(204, 204, 204));
        put("Teal", new Color(0, 102, 102));
        put("Orange", new Color(255, 165, 0));
        put("Yellow", new Color(255, 255, 0));
    }};

    public static final HashMap<String, ActionCode> ACTIONS = new HashMap<>() {{
        put("Free", ActionCode.FREE);
        put("Line", ActionCode.LINE);
        put("Rectangle", ActionCode.RECTANGLE);
        put("Oval", ActionCode.OVAL);
        put("Circle", ActionCode.CIRCLE);
        put("Text", ActionCode.TEXT);
        put("Small Eraser", ActionCode.ERASE_S);
        put("Medium Eraser", ActionCode.ERASE_M);
        put("Large Eraser", ActionCode.ERASE_L);
    }};

    public WhiteBoard(Host host) {
        super();
        this.setBackground(Color.WHITE);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                pointer.setInBoard(true);
                pointer.update(e.getX(), e.getY(), action);
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                pointer.setInBoard(false);
                localTempDrawElement = null;
                repaint();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (action == ActionCode.FREE) {
                    pt2 = new MyPoint(e.getX(), e.getY());

                } else if (action == ActionCode.TEXT) {
                    String text = host.getGui().getText();
                    if (!text.equals("")) {
                        DrawElement s = new Text(color, new MyPoint(e.getX(), e.getY()), text);
                        host.broadcastShape(s);
                        synchronized (drawElements) {
                            drawElements.add(s);
                        }
                        repaint();
                    }

                } else if (action == ActionCode.ERASE_S) {
                    DrawElement s = new EraserSmall(new MyPoint(e.getX(), e.getY()));
                    host.broadcastShape(s);
                    synchronized (drawElements) {
                        drawElements.add(s);
                    }
                    repaint();

                } else if (action == ActionCode.ERASE_M) {
                    DrawElement s = new EraserMedium(new MyPoint(e.getX(), e.getY()));
                    host.broadcastShape(s);
                    synchronized (drawElements) {
                        drawElements.add(s);
                    }
                    repaint();

                } else if (action == ActionCode.ERASE_L) {
                    DrawElement s = new EraserLarge(new MyPoint(e.getX(), e.getY()));
                    host.broadcastShape(s);
                    synchronized (drawElements) {
                        drawElements.add(s);
                    }
                    repaint();

                } else {
                    pt1 = new MyPoint(e.getX(), e.getY());
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                pt2 = new MyPoint(e.getX(), e.getY());
                if (action == ActionCode.CIRCLE ||
                    action == ActionCode.OVAL ||
                    action == ActionCode.RECTANGLE ||
                    action == ActionCode.LINE
                ) {
                    DrawElement s = switch (action) {
                        case CIRCLE -> new Circle(color, pt1, pt2);
                        case OVAL -> new Oval(color, pt1, pt2);
                        case RECTANGLE -> new Rectangle(color, pt1, pt2);
                        case LINE -> new Line(color, pt1, pt2);
                        default -> null;
                    };
                    host.broadcastShape(s);
                    synchronized (drawElements) {
                        drawElements.add(s);
                    }
                    localTempDrawElement = null;
                    repaint();
                }

            }

        });

        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                DrawElement s;
                MyPoint tempPoint = new MyPoint(e.getX(), e.getY());;
                switch (action) {
                    case FREE -> {
                        pt1 = pt2;
                        pt2 = new MyPoint(e.getX(), e.getY());
                        s = new Line(color, pt1, pt2);
                        host.broadcastShape(s);
                        synchronized (drawElements) {
                            drawElements.add(s);
                        }
                    }
                    case ERASE_S -> {
                        s = new EraserSmall(new MyPoint(e.getX(), e.getY()));
                        host.broadcastShape(s);
                        synchronized (drawElements) {
                            drawElements.add(s);
                        }
                    }
                    case ERASE_M -> {
                        s = new EraserMedium(new MyPoint(e.getX(), e.getY()));
                        host.broadcastShape(s);
                        synchronized (drawElements) {
                            drawElements.add(s);
                        }
                    }
                    case ERASE_L -> {
                        s = new EraserLarge(new MyPoint(e.getX(), e.getY()));
                        host.broadcastShape(s);
                        synchronized (drawElements) {
                            drawElements.add(s);
                        }
                    }
                    case CIRCLE -> {
                        localTempDrawElement = new Circle(color, pt1, tempPoint);
                    }
                    case OVAL -> {
                        localTempDrawElement = new Oval(color, pt1, tempPoint);
                    }
                    case RECTANGLE -> {
                        localTempDrawElement = new Rectangle(color, pt1, tempPoint);
                    }
                    case LINE -> {
                        localTempDrawElement = new Line(color, pt1, tempPoint);
                    }
                }
                pointer.update(e.getX(), e.getY(), action);
                repaint();
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                pointer.update(e.getX(), e.getY(), action);
                if (action == ActionCode.TEXT) {
                    String text = host.getGui().getText();
                    localTempDrawElement = new Text(color, new MyPoint(e.getX(), e.getY()), text);
                }
                repaint();
            }
        });
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D)g;

        synchronized (drawElements) {
            for (DrawElement element : drawElements) {
                element.draw(g2d);
            }
        }

        if (localTempDrawElement != null) {
            localTempDrawElement.draw(g2d);
        }

        if (pointer.isInBoard()) {
            pointer.draw(g2d);
        }
    }

    public void addShape(DrawElement s) {
        synchronized (drawElements) {
            drawElements.add(s);
        }
        repaint();
    }

    public void setAction(ActionCode action) {
        this.action = action;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public ArrayList<DrawElement> getDrawElements() {
        synchronized (drawElements) {
            return drawElements;
        }
    }

    public void setDrawElements(ArrayList<DrawElement> drawElements) {
        synchronized (drawElements) {
            this.drawElements = drawElements;
        }
        repaint();
    }
}
