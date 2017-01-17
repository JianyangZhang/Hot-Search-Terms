This project firstly generates an n-gram library from training data, then builds a language model based on the n-gram library
and stores the model to mySQL database. This language model can be used to predict the words after a given word or phrase.

**<h2>Search Engine Autocompletion:</h2>**<br/>
![show1](https://cloud.githubusercontent.com/assets/22739177/21952660/c13bd76e-d9d7-11e6-8b95-2e9a706b0dbc.PNG)<br/>
The following words can be predicted along with the user's typing.<br/>
The predictions are based on the probabilities in natural language that specific words usually appear after a given word or phrase.<br/>

**<h2>Sample mySQL database setup:</h2>**<br/>
mysql> create database tp;<br/>
mysql> use tp;<br/>
mysql> create table LanguageModel(starter varchar(250), follower varchar(250), probability int);<br/>
mysql> grant all on \*.\* to "root"@"%" identified by "123456";<br/>
mysql> flush privileges;<br/>
![tp1](https://cloud.githubusercontent.com/assets/22739177/21748178/f31d7eda-d532-11e6-8990-3459fb19bfe3.PNG)<br/>

**<h2>Preparation in HDFS:</h2>**<br/>
hdfs dfs -mkdir /mysql<br/>
hdfs dfs -put ./mysql-connector-java-5.1.39-bin.jar /mysql<br/>
hdfs dfs -mkdir /trainingdata<br/>
hdfs dfs -put ./trainingdata/* /trainingdata<br/>
![tp2](https://cloud.githubusercontent.com/assets/22739177/21748179/f334987c-d532-11e6-8e1f-00c01b09796f.PNG)

**<h2>Run the project:</h2>**<br/>
Usage: hadoop jar \<jar file\> \<main class name\> \<input dir\> \<output dir\> [GRAM_NUMBER] [THRESHOLD] [TOP_K]<br/>
Ex: hadoop jar TextPrediction.jar com.textprediction.ngramlm.Dispatcher /trainingdata /ngram 5 5 5<br/>

*GRAM_NUMBER:* the number n of the n-gram; default 5;<br/>
*THRESHOLD:* threshold for a phrase to be semantic; default 5;<br/>
*TOP_K:* only show the top k predictions based on their probabilities; default 5;<br/>

**<h2>Check the first mapreduce job results that generate an n-gram library:</h2>**<br/>
hdfs dfs -ls /ngram<br/>
hdfs dfs -get /ngram/part-r-* ./generated-n-gram-library/<br/>
![tp3](https://cloud.githubusercontent.com/assets/22739177/21748181/f339a3f8-d532-11e6-89f2-1983d254da05.PNG)<br/>

**<h2>The second mapreduce job produces a language model looks like this in mySQL database:</h2>**<br/>
select \* from LanguageModel limit 50;<br/>
select \* from LanguageModel into outfile '/tmp/generated-language-model/LanguageModel.out';<br/>
![tp4](https://cloud.githubusercontent.com/assets/22739177/21748180/f339a556-d532-11e6-9968-aa9cc21e48a0.PNG)<br/>

**<h2>Sample predictions:</h2>**<br/>
mySQL> select * from LanguageModel where starter like 'input%' order by probability desc limit x;<br/>
&nbsp;&nbsp;user input <b>*would*</b>, predictions are<br/>
![tp8](https://cloud.githubusercontent.com/assets/22739177/21757961/31648bd0-d5eb-11e6-9e80-100239cf3f6d.PNG)<br/>
&nbsp;&nbsp;user input <b>*this is*</b>, predictions are<br/>
![tp5](https://cloud.githubusercontent.com/assets/22739177/21748184/f33c40b8-d532-11e6-8b5c-71003ea384f5.PNG)<br/>
&nbsp;&nbsp;user input <b>*away from*</b>, predictions are<br/>
![tp6](https://cloud.githubusercontent.com/assets/22739177/21748182/f33a017c-d532-11e6-85f2-0d791087da9b.PNG)<br/>



