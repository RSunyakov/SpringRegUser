
import java.io.*;
import java.util.*;

public class Algorithm {
    public static void main(String[] args) throws IOException {
        //Этот индекс будет использоваться для выдачи индексов продуктам (0, 1, 2, ...)
        int index = 0;
        BufferedReader br = new BufferedReader(new FileReader("transactions.csv"));
        String tmp = null;

        //Создаю корзины, в которые сложу продукты
        Map<Long, List<String>> carts = new HashMap<>();
        //Создаю словарь для подсчета частоты одиночных продуктов
        Map<String, Integer> singleProdFrq = new HashMap<>();
        br.readLine();

        //Читаю файл и заполняю перечисленные выше словари: корзины и частоты одиночных продуктов
        while ((tmp = br.readLine()) != null) {
            long b_id = Long.parseLong(tmp.substring(tmp.indexOf(";") + 1));
            String prod_id = tmp.substring(0, tmp.indexOf(";"));

            if(!carts.containsKey(b_id)) {
                carts.put(b_id, new ArrayList<>());
            }

            carts.get(b_id).add(prod_id);

            if(!singleProdFrq.containsKey(prod_id)) {
                singleProdFrq.put(prod_id, 1);
            } else {
                singleProdFrq.put(prod_id, singleProdFrq.get(prod_id) + 1);
            }
        }

        //1st Filter
        //Здесь я сразу отсекаю нечастые продукты, т.к. дальше они нам не нужны
        Map<String, Integer> newSingleProdFrq = new HashMap<>();

        //Создаю словарь для каждого продукта, чтоб выдать им индексы (нужно будет в подсчете хеша
        Map<String, Integer> prodIndexes = new HashMap<>();

        //Сам процесс первой фильтрации, который проходят продукты, встречающиеся 3 и более раз
        for(Map.Entry<String, Integer> entry : singleProdFrq.entrySet()) {
            if(entry.getValue() >= 3) {
                newSingleProdFrq.put(entry.getKey(), entry.getValue());
                prodIndexes.put(entry.getKey(), index++);
            }
        }
        singleProdFrq = newSingleProdFrq;

        //Создаю сет пар и словарь для количества пар
        //*На момент написания комментариев понимаю, что сет уже тут не нужен, но переписывать программу не буду
        Set<Pair> pairs = new HashSet<>();
        Map<Pair, Integer> pairsCounts = new HashMap<>();

        //Создаю все возможные уникальные пары из всего начального сета
        //Здесь я прохожусь по каждой корзине
        for(List<String> cart : carts.values()) {
            String[] array = new String[cart.size()];
            cart.toArray(array);
            //В каждой корзине прохожусь по всем элементам
            for(int i = 0; i < array.length; i++) {
                //Прохожусь по всем остальным элементам в карзине (не трогая предыдущие, они уже были рассмотрены)
                for(int j = i + 1; j < array.length; j++) {
                    //Отсекаю те пары, в которых есть нечастый элемент
                    if(!singleProdFrq.containsKey(array[i]) || !singleProdFrq.containsKey(array[j]))
                        continue;

                    //Создаю пару, помещаю в сет (гарантирует уникальность пары) и считаю количество встреч этой пары
                    Pair p = new Pair(array[i], array[j]);
                    if(pairs.contains(p)) {
                        pairsCounts.put(p, pairsCounts.get(p) + 1);
                    }
                    pairs.add(p);
                    if(!pairsCounts.containsKey(p))
                        pairsCounts.put(p, 1);
                }
            }
        }

        //hash = (i+j)%2600
        //hash = (i+2j)%2600
        //Создаю два бакета размерами в 2600
        List<List<Pair>> buckets1= new ArrayList<>();
        List<List<Pair>> buckets2 = new ArrayList<>();

        for(int i = 0; i < 2600; i++){
            buckets1.add(new ArrayList<>());
            buckets2.add(new ArrayList<>());
        }

        //Теперь прохожусь по всем парам и расскидываю их по бакетам по принципу алгоритма
        //Считаю их хеш, помещаю пару в бакет с ид хеша
        for(Pair pair : pairs) {
            int hash = (prodIndexes.get(pair.prod1) + prodIndexes.get(pair.prod2))%2600;
            buckets1.get(hash).add(pair);
            hash = (prodIndexes.get(pair.prod1) + 2*prodIndexes.get(pair.prod2))%2600;
            buckets2.get(hash).add(pair);
        }

        //Создаю массив булевых переменных, обозначающих частоту бакета (true - частый, false - нечастый)
        boolean bucket1Frq[] = new boolean[2600];
        boolean bucket2Frq[] = new boolean[2600];

        //Прохожусь по всем бакетам
        for(int i = 0; i < 2600; i++) {
            ArrayList<Pair> pairs1 = (ArrayList<Pair>) buckets1.get(i);
            int frqInd = 0;
            //Считаю сумму частот пар в бакете
            for(Pair p : pairs1) {
                frqInd += pairsCounts.get(p);
            }
            //Если частотность бакета больше или равна трем, значение выставляется в true
            bucket1Frq[i] = frqInd >= 3;

            //То же самое для второго бакета
            ArrayList<Pair> pairs2 = (ArrayList<Pair>) buckets2.get(i);
            frqInd = 0;
            for(Pair p : pairs2) {
                frqInd += pairsCounts.get(p);
            }
            bucket2Frq[i] = frqInd >= 3;
        }

        //Выделяю итоговый сет пар, в котором остаются только частые пары
        Set<Pair> lastSetOfPairs = new HashSet<>();

        for(Pair p : pairs) {
            //Для удобства еще раз считаю хеши, которые являются индексами бакетов
            int ind1 = (prodIndexes.get(p.prod1) + prodIndexes.get(p.prod2))%2600;
            int ind2 = (prodIndexes.get(p.prod1) + 2*prodIndexes.get(p.prod2))%2600;

            //Проверяю, что оба бакета, в к-х находится пара, *частые* и что элементы этой пары так же частые
            //(последнее на всякий случай, по сути это гарантируется первой фильтрацией)
            if(bucket1Frq[ind1] && bucket2Frq[ind2] && singleProdFrq.containsKey(p.prod1) && singleProdFrq.containsKey(p.prod2))
                lastSetOfPairs.add(p);
        }

        //Вывожу
        PrintWriter pw = new PrintWriter(new FileOutputStream("output.txt"));
        pw.println(singleProdFrq.toString());
        for(Pair p : lastSetOfPairs) {
            pw.println(p.toString());
        }
        pw.flush();

        System.out.println(singleProdFrq.size());
        System.out.println(lastSetOfPairs.size());
    }

    static class Pair implements Comparable<Pair> {
        String prod1;
        String prod2;

        public Pair(String prod1, String prod2) {
            this.prod1 = prod1;
            this.prod2 = prod2;
        }

        //Функция сравнения двух пар, которая даст равенство при условии что в парах одинаковые продукты
        //Т.е. гарантируется, что пары Prod1,Prod2 и Prod2,Prod1 одинаковы
        @Override
        public int compareTo(Pair o) {
            return (prod1.compareTo(o.prod1) + prod2.compareTo(o.prod2))*(prod1.compareTo(o.prod2) + prod2.compareTo(o.prod1));
        }

        @Override
        public String toString() {
            return "Pair{" +
                    "prod1='" + prod1 + '\'' +
                    ", prod2='" + prod2 + '\'' +
                    '}';
        }
    }
}



