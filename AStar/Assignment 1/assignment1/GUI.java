package assignment1;

import javax.swing.*;

import java.awt.*;
import java.awt.geom.AffineTransform;

public class GUI {
  private SearchAlgo search;
  public SearchAlgo searchAlgo() { return search; }

  private Visualization vis;

  private boolean astar;

  private JTextField xInput, yInput;
  private JLabel f, g, h;
  private JPanel controls;

  public GUI(Grid grid) {
    astar = true;
    search = new AStar(grid);
    vis = new Visualization(search.state(), grid);

    xInput = new JTextField();
    yInput = new JTextField();

    g = new JLabel();
    h = new JLabel();
    f = new JLabel();

    setUpControls();
    changeViewingVertex(grid.goal);
  }

  public void openWindow() {
    JFrame frame = new JFrame("Search Visualization");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    Container pane = frame.getContentPane();
    pane.add(new JScrollPane(vis), BorderLayout.CENTER);
    pane.add(controls, BorderLayout.LINE_END);
    frame.pack();

    Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
    frame.setSize(
      (int)Math.min(dim.width * 0.75, Math.max(vis.getPreferredSize().getWidth(), 500)),
      (int)Math.min(dim.height * 0.75, Math.max(vis.getPreferredSize().getHeight(), 500))
    );

    frame.setVisible(true);
  }

  private void resetSearch() {
    Grid grid = search.grid();
    if (astar) {
      search = new AStar(grid);
    } else {
      search = new ThetaStar(grid);
    }
    vis.setStateAndGrid(search.state(), grid);
    changeViewingVertex(grid.goal);
  }

  private void updateValues(int vertex) {
    double gVal = search.state().g(vertex);
    double hVal = search.h(vertex);
    g.setText(String.format("g = %.3f", gVal));
    h.setText(String.format("h = %.3f", hVal));
    f.setText(String.format("f = %.3f", gVal + hVal));
  }

  private void changeViewingVertex(int vertex) {
    xInput.setText(Integer.toString(search.grid().xCoord(vertex)));
    yInput.setText(Integer.toString(search.grid().yCoord(vertex)));

    updateValues(vertex);
  }

  private void setUpControls() {
    xInput.setColumns(3);
    xInput.addActionListener(e -> {
      try {
        int x = Integer.parseInt(xInput.getText());
        int y = Integer.parseInt(yInput.getText());
        updateValues(searchAlgo().grid().indexFromCoords(x, y));
      } catch (NumberFormatException ex) { }
    });

    yInput.setColumns(3);
    yInput.addActionListener(e -> {
      try {
        int x = Integer.parseInt(xInput.getText());
        int y = Integer.parseInt(yInput.getText());
        updateValues(searchAlgo().grid().indexFromCoords(x, y));
      } catch (NumberFormatException ex) { }
    });

    JPanel coords = new JPanel();
    coords.add(new JLabel("("));
    coords.add(xInput);
    coords.add(new JLabel(","));
    coords.add(yInput);
    coords.add(new JLabel(")"));

    g.setAlignmentX(Component.LEFT_ALIGNMENT);
    h.setAlignmentX(Component.LEFT_ALIGNMENT);
    f.setAlignmentX(Component.LEFT_ALIGNMENT);

    JPanel top = new JPanel();
    top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
    top.add(coords);
    top.add(g);
    top.add(h);
    top.add(f);


    JRadioButton aStar = new JRadioButton("A*", true);
    aStar.addActionListener(e -> {
      if (aStar.isSelected()) {
        astar = true;
        resetSearch();
      }
    });

    JRadioButton thetaStar = new JRadioButton("Theta*", false);
    thetaStar.addActionListener(e -> {
      if (thetaStar.isSelected()) {
        astar = false;
        resetSearch();
      }
    });

    ButtonGroup algo = new ButtonGroup();
    algo.add(aStar);
    algo.add(thetaStar);

    JButton step = new JButton("Step");
    step.addActionListener(e -> {
      SearchAlgo search = searchAlgo();
      SearchState state = search.state();
      if (!state.finished) {
        search.runStep();
        vis.repaint();
        changeViewingVertex(state.lastPoppedVertex());
      }
    });

    JButton run = new JButton("Run");
    run.addActionListener(e -> {
      SearchAlgo search = searchAlgo();
      SearchState state = search.state();
      if (!state.finished) {
        while (!state.finished) {
          search.runStep();
        }
        vis.setLocation(0, 0);
        vis.repaint();
        changeViewingVertex(state.lastPoppedVertex());
      }
    });

    JButton reset = new JButton("Reset");
    reset.addActionListener(e -> resetSearch());

    JPanel center = new JPanel();
    Dimension gap = new Dimension(0, 10);
    center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
    center.add(aStar);
    center.add(thetaStar);
    center.add(Box.createRigidArea(gap));
    center.add(step);
    center.add(Box.createRigidArea(gap));
    center.add(run);
    center.add(Box.createRigidArea(gap));
    center.add(reset);


    JButton zoomIn = new JButton("+");
    zoomIn.addActionListener(e -> vis.zoomIn());
    zoomIn.setToolTipText("Zoom In");

    JButton zoomOut = new JButton("-");
    zoomOut.addActionListener(e -> vis.zoomOut());
    zoomOut.setToolTipText("Zoom Out");

    JPanel zoom = new JPanel();
    zoom.add(zoomIn);
    zoom.add(zoomOut);

    JButton repaint = new JButton("Repaint");
    repaint.addActionListener(e -> vis.repaint());

    JPanel bottom = new JPanel();
    bottom.setLayout(new BoxLayout(bottom, BoxLayout.Y_AXIS));
    bottom.add(zoom);
    bottom.add(wrap(repaint));

    controls = new JPanel();
    controls.setLayout(new BoxLayout(controls, BoxLayout.Y_AXIS));
    controls.add(wrap(top));
    controls.add(wrap(center));
    controls.add(wrap(bottom));
  }

  private static JPanel wrap(Component p) {
    JPanel wrap = new JPanel();
    wrap.add(p);
    return wrap;
  }
}

class Visualization extends JPanel {
  public static final int CellWidth = 50;
  public static final int LineWidth = 2;
  public static final int PathWidth = 4;
  public static final int VertexWidth = 10;
  public static final int SpecialVertexWidth = 25;

  private SearchState state;
  private Grid grid;
  private double scale;

  public Visualization(SearchState state, Grid grid) {
    this.state = state;
    this.grid = grid;
    scale = 0.5;
    setSize();
  }

  private void refresh() {
    setSize();
    setLocation(0, 0);
    repaint();
    revalidate();
  }

  public void setStateAndGrid(SearchState state, Grid grid) {
    this.state = state;
    this.grid = grid;
    refresh();
  }

  public void zoomIn() {
    scale *= 2.0;
    refresh();
  }

  public void zoomOut() {
    scale /= 2.0;
    refresh();
  }

  private double dimension(int numCells) {
    return numCells * (Visualization.CellWidth + 1) * scale;
  }

  private void setSize() {
    this.setPreferredSize(new Dimension(
      (int)dimension(grid.numCols()),
      (int)dimension(grid.numRows())
    ));
  }

  public void paint(Graphics graphics) {
    super.paint(graphics);

    Graphics2D g = (Graphics2D) graphics;

    if (g.getTransform().getScaleX() != scale) {
      AffineTransform transform = new AffineTransform(g.getTransform());
      transform.setToScale(scale, scale);
      g.setTransform(transform);
    }

    int numCols = grid.numCols();
    int numRows = grid.numRows();

    // Draw cell borders
    g.setStroke(new BasicStroke(LineWidth));
    g.setColor(Color.BLACK);
    for (int x = 1; x <= numCols + 1; x++) {
      drawLine(g, x, 1, x, numRows + 1);
    }
    for (int y = 1; y <= numRows + 1; y++) {
      drawLine(g, 1, y, numCols + 1, y);
    }

    // Fill blocked cells
    g.setColor(Color.gray);
    for (int y = 1; y <= numRows; y++) {
      for (int x = 1; x <= numCols; x++) {
        if (grid.isBlocked(grid.indexFromCoords(x, y))) {
          g.fillRect(
            x * CellWidth + LineWidth / 2,
            y * CellWidth + LineWidth / 2,
            CellWidth - LineWidth,
            CellWidth - LineWidth
          );
        }
      }
    }

    g.setStroke(new BasicStroke(PathWidth));

    // Draw verticies in the open list
    g.setColor(Color.CYAN);
    state.fringeEntries().forEach(v -> {
      int x = grid.xCoord(v);
      int y = grid.yCoord(v);
      int parent = state.parent(v);
      drawLine(g, x, y, grid.xCoord(parent), grid.yCoord(parent));
      drawVertex(g, x, y);
    });

    // Draw verticies in the closed list
    g.setColor(Color.yellow);
    state.visitedVertices().forEach(v -> {
      int x = grid.xCoord(v);
      int y = grid.yCoord(v);
      int parent = state.parent(v);
      drawLine(g, x, y, grid.xCoord(parent), grid.yCoord(parent));
      drawVertex(g, x, y);
    });

    if (state.finished) {
      // Draw min cost path
      int vertex = grid.goal;
      int parent = state.parent(vertex);
      if (parent != SearchState.NoParent) {
        int vx = grid.xCoord(vertex);
        int vy = grid.yCoord(vertex);
        int px = grid.xCoord(parent);
        int py = grid.yCoord(parent);
        g.setColor(Color.RED);
        while (vertex != grid.start) {
          drawLine(g, vx, vy, px, py);
          drawSpecialVertex(g, vx, vy);
          vertex = parent;
          vx = px;
          vy = py;
          parent = state.parent(vertex);
          px = grid.xCoord(parent);
          py = grid.yCoord(parent);
        }
      }
    } else {
      // Draw the current vertex
      int current = state.lastPoppedVertex();
      g.setColor(Color.MAGENTA);
      drawSpecialVertex(g, grid.xCoord(current), grid.yCoord(current));
    }

    // Draw start vertex
    g.setColor(new Color(0, 160, 0));
    drawSpecialVertex(g, grid.xCoord(grid.start), grid.yCoord(grid.start));

    // Draw goal vertex
    g.setColor(Color.BLUE);
    drawSpecialVertex(g, grid.xCoord(grid.goal), grid.yCoord(grid.goal));
  }

  public static void drawLine(Graphics2D g, int x1, int y1, int x2, int y2) {
    g.drawLine(x1 * CellWidth, y1 * CellWidth, x2 * CellWidth, y2 * CellWidth);
  }

  public static void drawVertex(Graphics2D g, int x, int y) {
    drawVertex(g, x, y, VertexWidth);
  }

  public static void drawSpecialVertex(Graphics2D g, int x, int y) {
    drawVertex(g, x, y, SpecialVertexWidth);
  }

  private static void drawVertex(Graphics2D g, int x, int y, int width) {
    g.fillArc(
      x * CellWidth - width / 2,
      y * CellWidth - width / 2,
      width,
      width,
      0,
      360);
  }
}
