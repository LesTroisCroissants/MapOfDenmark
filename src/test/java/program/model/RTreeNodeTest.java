package program.model;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import program.shared.MapElement;
import program.shared.MapFillable;
import program.shared.MapPoint;
import program.shared.Point;

import static org.junit.jupiter.api.Assertions.*;

class RTreeNodeTest {

    RTreeNode node;

    @Test
    void addAndGetElement(){
        MapElement me = new MapPoint(new Point(0,0), "");
        node.addElement(me);

        //assertEquals(me, node.getElements[0]);
    }

    @Test
    void addAndGetChild(){
        RTreeNode rTreeNode = new RTreeNode(3);
        node.addChild(rTreeNode);

        //assertEquals(rTreeNode, node.getChildren[0]);
    }

    @Test
    void updateBoundingBoxTest(){
        MapFillable mf = new MapFillable("", 3);
        mf.add(new Point(0.5F,1));
        mf.add(new Point(2,2));
        mf.add(new Point(1,0.5F));
        node.addElement(mf);

        assertEquals(2, node.getMaxPoint()[0]);
        assertEquals(2, node.getMaxPoint()[1]);
        assertEquals(0.5F, node.getMinPoint()[0]);
        assertEquals(0.5F, node.getMinPoint()[1]);

        mf = new MapFillable("", 3);
        mf.add(new Point(1.5F, 0.5F));
        mf.add(new Point(2, 1.5F));
        mf.add(new Point(3, 1));
        node.addElement(mf);

        assertEquals(3, node.getMaxPoint()[0]);
        assertEquals(2, node.getMaxPoint()[1]);
        assertEquals(0.5F, node.getMinPoint()[0]);
        assertEquals(0.5F, node.getMinPoint()[1]);

        mf = new MapFillable("", 3);
        mf.add(new Point(1, 3));
        mf.add(new Point(1, 1));
        mf.add(new Point(0, 0));
        node.addElement(mf);

        assertEquals(3, node.getMaxPoint()[0]);
        assertEquals(3, node.getMaxPoint()[1]);
        assertEquals(0, node.getMinPoint()[0]);
        assertEquals(0, node.getMinPoint()[1]);
    }

    @BeforeEach
    void setUp() {
        node = new RTreeNode(3);
    }

    @AfterEach
    void tearDown() {
        node = null;
    }
}