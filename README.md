**Autocompletion:**
The autocompletion program firstly generates an n-gram library from training data, then builds a language model based on the n-gram library and stores the model to MySQL database. This language model can be used to predict the words after a given word or phrase. Details are shown in the following sections.

**Spell checking:**
The spell checking program uses a Trie as data structure, takes single words as input and construct a dictionary with probability.
With the word "best" being inserted, "bost", "bast", "bist", "bust" etc. will all point to "best" as the correct word. If "bust" is added later, it will overwrite the correct spelling of "best" to be "bust".

**<h2>Search Engine Autocompletion:</h2>**
![show1](https://cloud.githubusercontent.com/assets/22739177/21952660/c13bd76e-d9d7-11e6-8b95-2e9a706b0dbc.PNG)
The following words can be predicted along with the user's typing.
The predictions are based on the probabilities in natural language that specific words usually appear after a given word or phrase.<br/>

**<h2>Search Engine spell checking:</h2>**
![1506302661_tmp_show3](https://user-images.githubusercontent.com/22739177/30788820-ade46b4a-a155-11e7-92a3-21fdef42315b.jpg)
The most regular function a search engine provides. When a user's input may have typo, it suggests the correct spelling

**<h2>Sample MySQL database setup:</h2>**
mysql> create database tp;<br/>
mysql> use tp;<br/>
mysql> create table LanguageModel(starter varchar(250), follower varchar(250), probability int);<br/>
mysql> grant all on \*.\* to "root"@"%" identified by "123456";<br/>
mysql> flush privileges;<br/>

![tp1](https://cloud.githubusercontent.com/assets/22739177/21748178/f31d7eda-d532-11e6-8990-3459fb19bfe3.PNG)

**<h2>Preparation in HDFS:</h2>**
hdfs dfs -mkdir /mysql<br/>
hdfs dfs -put ./mysql-connector-java-5.1.39-bin.jar /mysql<br/>
hdfs dfs -mkdir /trainingdata<br/>
hdfs dfs -put ./trainingdata/* /trainingdata<br/>

![tp2](https://cloud.githubusercontent.com/assets/22739177/21748179/f334987c-d532-11e6-8e1f-00c01b09796f.PNG)

**<h2>Run the project:</h2>**
Usage: hadoop jar \<jar file\> \<main class name\> \<input dir\> \<output dir\> [GRAM_NUMBER] [THRESHOLD] [TOP_K]<br/>
Ex: hadoop jar TextPrediction.jar com.textprediction.ngramlm.Dispatcher /trainingdata /ngram 5 5 5<br/>

*GRAM_NUMBER:* the number n of the n-gram; default 5;<br/>
*THRESHOLD:* threshold for a phrase to be semantic; default 5;<br/>
*TOP_K:* only show the top k predictions based on their probabilities; default 5;<br/>

**<h2>Check the first mapreduce job results that generate an n-gram library:</h2>**
hdfs dfs -ls /ngram<br/>
hdfs dfs -get /ngram/part-r-* ./generated-n-gram-library/<br/>

![tp3](https://cloud.githubusercontent.com/assets/22739177/21748181/f339a3f8-d532-11e6-89f2-1983d254da05.PNG)

**<h2>The second mapreduce job produces a language model looks like this in MySQL database:</h2>**
select \* from LanguageModel limit 50;<br/>
select \* from LanguageModel into outfile '/tmp/generated-language-model/LanguageModel.out';<br/>

![tp4](https://cloud.githubusercontent.com/assets/22739177/21748180/f339a556-d532-11e6-9968-aa9cc21e48a0.PNG)

**<h2>Sample predictions:</h2>**
mysql> select * from LanguageModel where starter like 'input%' order by probability desc limit x;<br/>

&nbsp;&nbsp;user input <b>*would*</b>, predictions are<br/>

![tp8](https://cloud.githubusercontent.com/assets/22739177/21757961/31648bd0-d5eb-11e6-9e80-100239cf3f6d.PNG)<br/>
&nbsp;&nbsp;user input <b>*this is*</b>, predictions are<br/>

![tp5](https://cloud.githubusercontent.com/assets/22739177/21748184/f33c40b8-d532-11e6-8b5c-71003ea384f5.PNG)<br/>
&nbsp;&nbsp;user input <b>*away from*</b>, predictions are<br/>

![tp6](https://cloud.githubusercontent.com/assets/22739177/21748182/f33a017c-d532-11e6-85f2-0d791087da9b.PNG)<br/>



