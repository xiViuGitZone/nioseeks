package com.tends.nioseeks.designmode.decorators;

//装饰器模式（Decorator Pattern）：向一个现有对象添加新功能，同时不改变其结构。这种类型设计模式属于结构型模式，它是作为现有的类的一个包装
public class TestDecorators {
    //动态地给一个对象添加一些额外的职责。就增加功能来说，装饰器模式相比生成子类更为灵活
    //使用场景： 1、扩展一个类的功能。 2、动态增加功能，动态撤销   可代替继承


    interface Shape {
        void draw();
    }
    static class Rectangle implements Shape {
        @Override
        public void draw() {
            System.out.println("Shape: Rectangle");
        }
    }
    static class Circle implements Shape {
        @Override
        public void draw() {
            System.out.println("Shape: Circle");
        }
    }
    //abstract class ShapeDecorator implements Shape {
    static class ShapeDecorator implements Shape {
        protected Shape decoratedShape;

        public ShapeDecorator(Shape decoratedShape){
            this.decoratedShape = decoratedShape;
        }
        public void draw(){
            decoratedShape.draw();
        }
    }
    static class RedShapeDecorator extends ShapeDecorator {

        public RedShapeDecorator(Shape decoratedShape) {
            super(decoratedShape);
        }
        @Override
        public void draw() {
            decoratedShape.draw();
            setRedBorder(decoratedShape);
        }
        private void setRedBorder(Shape decoratedShape){
            System.out.println("Border Color: Red");
        }
    }




    public static void main(String[] args) {
        Shape circle = new Circle();
        ShapeDecorator redCircle = new RedShapeDecorator(new Circle());
        ShapeDecorator redRectangle = new RedShapeDecorator(new Rectangle());
        System.out.println("Circle with normal border");
        circle.draw();
        System.out.println("\nCircle of red border");
        redCircle.draw();
        System.out.println("\nRectangle of red border");
        redRectangle.draw();
    }










    
}
