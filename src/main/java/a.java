public class a {
        public static <T> T max(T[] array, Comparator<T> comparator){...}

        public static void main(String[] args) {
            Integer[] ints = {1,4,3};
            Cow[] cows = {new Cow(7,50), new Cow(9,200), new Cow(3,100)};

            System.out.println(max(ints, new Comparator<Integer>(){
                public int compare(Integer o1, Integer o2) {
                    return o1 - o2;
                }
            })); // prints 4

            System.out.println(max(cows, new Comparator<Cow>(){
                public int compare(Cow o1, Cow o2) {
                    return o1.getAge() - o2.getAge();
                }
            }).getAge()); // prints 9

        }
    }
}
