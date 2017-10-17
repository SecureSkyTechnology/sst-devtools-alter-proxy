# sst-devtools-alter-proxy

local http proxy alternating http contents to local file contents.

- ホスト名 + Path をローカルディレクトリにマッピングし、もしリクエストされたファイルがローカルにあれば、そちらをレスポンスとして返すローカルHTTPプロキシです。
- このプロキシを通せば、ローカルのHTML/CSS/JSの修正内容をサーバにUPしなくても、すぐにブラウザ上で確認できます。
  - PHPなど動的なリクエストはサーバにそのままプロキシされますので、アプリが動く状態で確認できるようになります。
- Web制作やWebアプリ開発のお供にご利用ください。

## requirement

* Java8

## 使い方

1. jarファイルをDLし、ダブルクリックして起動します。
2. addボタンをクリックし、ホスト名 + Path に対して、マッピングするローカルディレクトリを登録します。
   - `target host` : ホスト名を入力します。(wild-cardやregexpは利用できません)
   - `path prefix` : マッピングしたいPathを入力します。必ず末尾を "/" で終わらせてください。ルートpathをマッピングしたい場合は "/" 一文字を入力します。
   - `local directory` : マッピングするローカルディレクトリを選択します。
   - `"/" handling` : "/" で終わった場合のマッピング動作を選択します。マッピングせずそのままオリジンサーバにプロキシするか、もしローカルに index.html があればそちらを優先するかを選択できます。
   - `filename extensions` : マッピング対象のファイル名拡張子をホワイトリストで入力します。"."(ドット)は不要、複数入力する場合は","(カンマ)で区切ってください。
   - `text charset` : mime-typeが "text/" で始まる拡張子および ".js" ファイルについて、`Content-Type` レスポンスヘッダーに含めるデフォルトのcharsetを選択してください。
3. `listening port` でproxyとしての待受ポート番号を設定します。
4. start / stop ボタンでproxyを起動/停止します。
   - 起動は一瞬ですが、停止は数秒かかります。

### 設定保存と保存先

- proxy起動時、およびアプリ終了時にその時点の設定(ポート番号とマッピング情報)が保存されます。
- 保存先 : `$HOME/.sst-devtools-alter-proxy.yml` 

### オススメの使い方

- 他の local http proxy と組み合わせると使いやすいです。
- 例えばブラウザのプロキシとしては Burp Suite ( https://portswigger.net/burp ) や Fiddler ( http://www.telerik.com/fiddler ) を設定し、その上流プロキシとして alter-proxy を設定します。
- そうすると、 Burp や Fiddler でHTTP通信の中身をチェックしつつ、alter-proxy でコンテンツを書き換えて、開発中のWebサイトのデザインや動作を心ゆくまで調整することができます。

## 開発環境

* JDK >= 1.8.0_92
* Eclipse >= 4.5.2 (Mars.2 Release), "Eclipse IDE for Java EE Developers" パッケージを使用
* Maven >= 3.3.9 (maven-wrapperにて自動的にDLしてくれる)
* ソースコードやテキストファイル全般の文字コードはUTF-8を使用

## ビルドと実行

```
cd sst-devtools-alter-proxy/

ビルド:
mvnw package

jarファイルから実行:
java -jar target/alter-proxy-xxx.jar

Mavenプロジェクトから直接実行:
mvnw exec:java
```

## Eclipseプロジェクト用の設定

https://github.com/SecureSkyTechnology/howto-eclipse-setup の `setup-type1` を使用。README.mdで以下を参照のこと:

* Ecipseのインストール
* Clean Up/Formatter 設定
* 必須プラグイン Lombok / オプションプラグイン Swing Designer のインストール 

Eclipseにインポートする:

1. gitでリポジトリをcloneする。
2. Eclipseを起動し、File -> Import を開く。
   1. import source で Maven -> Existing Maven Projects を選択
   2. Root Directory で本ディレクトリを選び、pom.xmlが認識されればそのままインポートできる。

 Swing Designer 備考:

* 初めてSwing Designerでフレームを作成し、レイアウトで `MigLayout` を選択したところ、Eclipse プロジェクト直下に `miglayout15-swing.jar` と `miglayout-src.zip` が自動でDLされ、Eclipse プロジェクトの Java Build Path にライブラリとして自動で追加されてしまった。
* Swing Designer が掴んでいたためか、Eclipse 起動中はこれらのファイルは完全には削除できなかった。
* →そのため、一旦Eclipseを終了させてファイルを削除したり、Eclipseプロジェクト プロパティのJava Build Path からこれらのjarを手作業で削除したりした。
* さらに、そのままでは `MigLayout` 関連のimportでエラーとなるため、pom.xml に同等の `com.miglayout:miglayout-swing:4.2` を追加してコンパイルエラーを解決した。

`MigLayout` で使用している `miglayout-swing` について(2017-09-27時点):

* http://www.miglayout.com/
* もともと http://www.migcalendar.com/ というJavaのGUIのカレンダーコンポーネントを開発している会社の製品。
* ライセンスとしてはBSD/GPLのデュアルライセンスなので、今回の利用には問題ないと判断した。(2017-09-27)
