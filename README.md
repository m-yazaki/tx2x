tx2x
====
tx2xは、テキストファイルから別のフォーマットのデータを作成するJavaアプリケーションです。
InDesignタグ付きテキストを生成したり、（Wordファイルを自動操縦して）Wordファイルを作成したりできます。

GitHubからソースコードをダウンロードする
-----
リモートリポジトリーのURLを指定して、Cloneしてください。

<table>
  <tr>
    <td>リモートリポジトリーのURL</td><td>https://github.com/m-yazaki/tx2x.git</td>
  </tr>
</table>

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

Gradleのインストールについては、以下のページをご覧ください。  
http://gradle.monochromeroad.com/docs/userguide/installation.html

実行する
-----
以下のように実行します。

```
java -classpath jacob.jar;Tx2x-1.2.jar tx2x.Tx2x -word example
```

カスタマイズ
=====
ソースコードを公開していますので、自由にカスタマイズできます。

スタイルが不足していた場合の対処方法
-----
###新たなスタイルを定義したい場合
たとえば行頭から始まる「=」を新たなスタイルとして利用したい場合は、以下のように対処します。
※Wordの「箇条書き＝」スタイルに対応させる場合を例に説明しています。

####(1)tx2x.StyleManagerにコードを追加する
```
m_cStyleList.add(new Style_BulletLike("【=】", "^=\t.*", "^[^=].*"));
```

####(2)tx2x.word.LongStyleManagerにコードを追加する
```
if (m_sCurrentLongStyle.equals("【=】【=】")) {
	return "箇条書き＝";
}
```

####(3)Tx2xWordTemplate.dotxに「箇条書き＝」スタイルを追加する
Wordのテンプレートファイルに「箇条書き＝」スタイルを定義します。

###既存のスタイルを組み合わせた、新しいスタイルが登場した場合
tx2xでテキストファイルを変換中に以下のようなエラーメッセージが表示されます。
```
【表】【行】【セル】【箇条書き・】【箇条書き・】は、標準スタイルで出力されました。
```
このような場合は、tx2x.word.LongStyleManagerにコードを追加すると、新しいスタイルを利用できるようになります。

カスタマイズのご依頼は…
-----
カスタマイズのご依頼もお引き受けいたします。カスタマイズ量が多くなりそうな場合や、対応可能かどうか判断が難しい場合は、有限会社ビートラストまでご相談ください。

http://www.b-trust.jp/contact-info

バージョンアップしたいこと
=====
* テストコードを追加する
