За основу взян алгоритм с хеш функциями **(i+j)mod 2600** и **(i+2j)mod 2600**
<h3> Вывод имеет вид: </h3>
Сначала идет массив пар **Продукт_Код**=<**Кол-во продуктов**> <p>
Пример: **PRD0901281=3**, **PRD0901280=9**, **PRD0901279=10**<p>

Далее в каждой строчке в формате **Pair{prod1='PRD0903050', prod2='PRD0904044'}** список пар частых продуктов (без их частот)<p>

Итого получилось, что частых товаров **2577** (это покупались 3 и более раз); и частых пар **147029** (все уникальные пары, которые были куплены 3 и более раз)
