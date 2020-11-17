package com.tends.nioseeks.designmode.facades;

//外观模式（Facade Pattern）：隐藏系统的复杂性并向客户端提供了一个客户端可以访问系统的接口。这种类型的设计模式属于结构型模式，
public class TestFacades {
    //使用场景： 1、为复杂的模块或子系统提供外界访问的模块。 2、子系统相对独立。 3、预防低水平人员带来的风险。
    //注意事项：在层次化结构中，可以使用外观模式定义系统中每一层的入口


    interface Shape {
        void draw();
    }
    static class Rectangle implements Shape {
        @Override
        public void draw() {
            System.out.println("Rectangle::draw()");
        }
    }
    static class Square implements Shape {
        @Override
        public void draw() {
            System.out.println("Square::draw()");
        }
    }
    static class Circle implements Shape {
        @Override
        public void draw() {
            System.out.println("Circle::draw()");
        }
    }

    static class ShapeMaker {  //创建一个外观类
        private Shape circle;
        private Shape rectangle;
        private Shape square;

        public ShapeMaker() {
            circle = new Circle();
            rectangle = new Rectangle();
            square = new Square();
        }
        public void drawCircle(){
            circle.draw();
        }
        public void drawRectangle(){
            rectangle.draw();
        }
        public void drawSquare(){
            square.draw();
        }
    }





    public static void main(String[] args) {
        ShapeMaker shapeMaker = new ShapeMaker();

        shapeMaker.drawCircle();
        shapeMaker.drawRectangle();
        shapeMaker.drawSquare();
    }


}
