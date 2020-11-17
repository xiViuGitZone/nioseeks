package com.tends.nioseeks.designmode.builds;

import java.util.ArrayList;
import java.util.List;

//建造者模式、使用多个简单的对象一步一步构建成一个复杂的对象。这种类型的设计模式属于创建型模式，它提供了一种创建对象的最佳方式
public class TestBuilds {
    //一些基本部件不会变，其组合经常变化时、使用场景：需要生成的对象具有复杂的内部结构、需要生成的对象内部属性本身相互依赖。

    interface Items {
        public String name();
        public Packing packing();
        public float price();
    }
    interface Packing {
        public String packed();
    }
    static class Wrapper implements Packing {
        @Override
        public String packed() {
            return "Wrapper Packing";
        }
    }

    //abstract class Burger implements Items {
    interface Burger extends Items {
        @Override
        public default Packing packing() {
            return new Wrapper();
        }
        @Override
        public abstract float price();
    }
    //static class VegBurger extends Burger {
    static class VegBurger implements Burger {
        @Override
        public float price() {
            return 25.0f;
        }
        @Override
        public String name() {
            return "Veg Burger";
        }
    }
    static class ChickenBurger implements Burger {
        @Override
        public float price() {
            return 50.5f;
        }
        @Override
        public String name() {
            return "Chicken Burger";
        }
    }

    static class MealBuilder {
        public Meal prepareVegMeal (){
            Meal meal = new Meal();
            meal.addItem(new VegBurger());
            return meal;
        }
        public Meal prepareNonVegMeal (){
            Meal meal = new Meal();
            meal.addItem(new ChickenBurger());
            return meal;
        }
    }
    static class Meal {
        private List<Items> item = new ArrayList<Items>();

        public void addItem(Items tms){
            item.add(tms);
        }
        public float getCost(){
            float cost = 0.0f;
            for (Items item : item) {
                cost += item.price();
            }
            return cost;
        }
        public void showItems(){
            for (Items item : item) {
                System.out.print("Item : "+item.name());
                System.out.print(", Packing : "+item.packing().packed());
                System.out.println(", Price : "+item.price());
            }
        }
    }



    public static void main(String[] args) {
        MealBuilder mealBuilder = new MealBuilder();

        Meal vegMeal = mealBuilder.prepareVegMeal();
        System.out.println("Veg Meal");
        vegMeal.showItems();
        System.out.println("Total Cost: " +vegMeal.getCost());

        Meal nonVegMeal = mealBuilder.prepareNonVegMeal();
        System.out.println("\n\nNon-Veg Meal");
        nonVegMeal.showItems();
        System.out.println("Total Cost: " +nonVegMeal.getCost());
    }

}
