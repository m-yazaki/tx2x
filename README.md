tx2x
====
tx2xは、テキストファイルから別のフォーマットのデータを作成するアプリケーションです。
InDesignタグ付きテキストを生成したり、（Wordファイルを自動操縦して）Wordファイルを作成したりできます。

GitHubからソースコードをダウンロードする
-----
リモートリポジトリーのURLを指定して、Cloneしてください。
|リモートリポジトリーのURL|https://github.com/m-yazaki/tx2x.git|

もちろん、Fork、Pull Requestも大歓迎です。

jacob.jarをダウンロードする
-----
Wordの自動操縦に、JACOB - Java COM Bridgeを使用しています。

以下のページからjacob-1.xx.zipをダウンロードしてください。
http://sourceforge.net/projects/jacob-project/

ダウンロードしたjacob-1.xx.zipを解凍し、以下のファイルをbuild.gradeファイルと同じフォルダーにコピーします。

* Windowsが32ビット版の場合
  * jacob.jar
  * jacob-1.17-x86.dll

* Windowsが64ビット版の場合
  * jacob.jar
  * jacob-1.17-x64.dll

ビルドする
-----
Gradleを使ってビルドできます。

```
gradle build
```

実行する
-----
以下のように実行します。

```
java -classpath jacob.jar;Tx2x-1.2.jar tx2x.Tx2x -word example
```
