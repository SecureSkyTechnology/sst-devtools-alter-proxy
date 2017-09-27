# sst-devtools-alter-proxy

local http proxy alternating http contents to local file contents.

- �z�X�g�� + Path �����[�J���f�B���N�g���Ƀ}�b�s���O���A�������N�G�X�g���ꂽ�t�@�C�������[�J���ɂ���΁A����������X�|���X�Ƃ��ĕԂ����[�J��HTTP�v���L�V�ł��B
- ���̃v���L�V��ʂ��΁A���[�J����HTML/CSS/JS�̏C�����e���T�[�o��UP���Ȃ��Ă��A�����Ƀu���E�U��Ŋm�F�ł��܂��B
  - PHP�ȂǓ��I�ȃ��N�G�X�g�̓T�[�o�ɂ��̂܂܃v���L�V����܂��̂ŁA�A�v����������ԂŊm�F�ł���悤�ɂȂ�܂��B
- Web�����Web�A�v���J���̂����ɂ����p���������B

## requirement

* Java8

## �g����

1. jar�t�@�C����DL���A�_�u���N���b�N���ċN�����܂��B
2. add�{�^�����N���b�N���A�z�X�g�� + Path �ɑ΂��āA�}�b�s���O���郍�[�J���f�B���N�g����o�^���܂��B
   - `target host` : �z�X�g������͂��܂��B(wild-card��regexp�͗��p�ł��܂���)
   - `path prefix` : �}�b�s���O������Path����͂��܂��B�K�������� "/" �ŏI��点�Ă��������B���[�gpath���}�b�s���O�������ꍇ�� "/" �ꕶ������͂��܂��B
   - `local directory` : �}�b�s���O���郍�[�J���f�B���N�g����I�����܂��B
   - `"/" handling` : "/" �ŏI������ꍇ�̃}�b�s���O�����I�����܂��B�}�b�s���O�������̂܂܃I���W���T�[�o�Ƀv���L�V���邩�A�������[�J���� index.html ������΂������D�悷�邩��I���ł��܂��B
   - `filename extensions` : �}�b�s���O�Ώۂ̃t�@�C�����g���q���z���C�g���X�g�œ��͂��܂��B"."(�h�b�g)�͕s�v�A�������͂���ꍇ��","(�J���})�ŋ�؂��Ă��������B
   - `text charset` : mime-type�� "text/" �Ŏn�܂�g���q����� ".js" �t�@�C���ɂ��āA`Content-Type` ���X�|���X�w�b�_�[�Ɋ܂߂�f�t�H���g��charset��I�����Ă��������B
3. `listening port` ��proxy�Ƃ��Ă̑Ҏ�|�[�g�ԍ���ݒ肵�܂��B
4. start / stop �{�^����proxy���N��/��~���܂��B
   - �N���͈�u�ł����A��~�͐��b������܂��B

### �ݒ�ۑ��ƕۑ���

- proxy�N�����A����уA�v���I�����ɂ��̎��_�̐ݒ�(�|�[�g�ԍ��ƃ}�b�s���O���)���ۑ�����܂��B
- �ۑ��� : `$HOME/.sst-devtools-alter-proxy.yml` 

### �I�X�X���̎g����

- ���� local http proxy �Ƒg�ݍ��킹��Ǝg���₷���ł��B
- �Ⴆ�΃u���E�U�̃v���L�V�Ƃ��Ă� Burp Suite ( https://portswigger.net/burp ) �� Fiddler ( http://www.telerik.com/fiddler ) ��ݒ肵�A���̏㗬�v���L�V�Ƃ��� alter-proxy ��ݒ肵�܂��B
- ��������ƁA Burp �� Fiddler ��HTTP�ʐM�̒��g���`�F�b�N���Aalter-proxy �ŃR���e���c�����������āA�J������Web�T�C�g�̃f�U�C���⓮���S�䂭�܂Œ������邱�Ƃ��ł��܂��B

## �J����

* JDK >= 1.8.0_92
* Eclipse >= 4.5.2 (Mars.2 Release), "Eclipse IDE for Java EE Developers" �p�b�P�[�W���g�p
* Maven >= 3.3.9 (maven-wrapper�ɂĎ����I��DL���Ă����)
* �\�[�X�R�[�h��e�L�X�g�t�@�C���S�ʂ̕����R�[�h��UTF-8���g�p

## �r���h�Ǝ��s

```
cd sst-devtools-alter-proxy/

�r���h:
mvnw package

jar�t�@�C��������s:
java -jar target/alter-proxy-xxx.jar

Maven�v���W�F�N�g���璼�ڎ��s:
mvnw exec:java
```

## Eclipse�v���W�F�N�g�p�̐ݒ�

### Eclipse��Lombok���C���X�g�[������

1. lombok.jar ���C���X�g�[�����Ď��s���AEclipse��Lombok���C���X�g�[������B
  * https://projectlombok.org/

�Q�l�F

* Lombok - Qiita
  * http://qiita.com/yyoshikaw/items/32a96332cc12854ca7a3
* Lombok �g�������� - Qiita
  * http://qiita.com/opengl-8080/items/671ffd4bf84fe5e32557

### Eclipse�ɃC���|�[�g����

1. git�Ń��|�W�g����clone����B
2. Eclipse���N�����AFile -> Import ���J���B
   1. import source �� Maven -> Existing Maven Projects ��I��
   2. Root Directory �Ŗ{�f�B���N�g����I�сApom.xml���F�������΂��̂܂܃C���|�[�g�ł���B

### Clean Up/Formatter �ݒ���C���|�[�g����

1. Window -> Preferences -> Java -> Code Style -> Clean Up -> Import... ����A sst-eclipse-mars2-cleanup.xml ���C���|�[�g����B(sst-eclipse-mars2-cleanup �Ƃ������O�œo�^�����)
2. Package Explorer ����v���W�F�N�g���E�N���b�N -> Properties ��I�����AJava Code Style -> Clean Up �� Enable project specific settings �Ƀ`�F�b�N�����Asst-eclipse-mars2-cleanup ��I������B
3. Window -> Preferences -> Java -> Code Style -> Formatter -> Import... ����A sst-eclipse-mars2-formatter.xml ���C���|�[�g����B(sst-eclipse-mars2-formatter �Ƃ������O�œo�^�����)
4. Package Explorer ����v���W�F�N�g���E�N���b�N -> Properties ��I�����AJava Code Style -> Formatter �� Enable project specific settings �Ƀ`�F�b�N�����Asst-eclipse-mars2-formatter ��I������B

### Swing Designer���g��

GUI�c�[���L�b�g�Ƃ���Java��Swing���g���Ă���BEclipse�ł���΁ASwing Designer���C���X�g�[������ƃO���t�B�J����Swing�̉�ʂ�݌v�ł���B

* https://projects.eclipse.org/projects/tools.windowbuilder
  * "Eclipse WindowBuilder" �� Swing Desginer ���܂܂�Ă���B

1. Help -> Install New Software �� "Work with:" �� `Mars - http://download.eclipse.org/releases/mars` (Mars�̏ꍇ)���v���_�E������I������B
   * �Ӑ}�Ƃ��ẮAEclipse�{�̂̃v���W�F�N�g�Ȃ̂ŁA�g�p���Ă���Eclipse�̃o�[�W�����ɉ����������̃����[�X�_�E�����[�hURL��I������B
2. "Swing Designer" �Ńt�B���^���A"Swing Designer" �Ƀ`�F�b�N�����ăC���X�g�[������B
3. ������Swing�R���|�[�l���g��Java�\�[�X���J�����́A"Open With" => "WindowBuilder Editor" �ŊJ���B

�g�����̎Q�l�L���F

* �J������ SwingDesigner�̃C���X�g�[���Ǝg�p
  * http://developmentmemo.blog.fc2.com/blog-entry-140.html
* java�Œ��ȒP��GUI���쐬���邽�߂�Eclipse�v���O�C���uSwingDesigner�v �C���X�g�[�� - ���߂������
  * http://konbu13.hatenablog.com/entry/2013/12/25/230637
* �uSwingDesigner�v��Swing�A�v���P�[�V���������낤! ����2�`�A�v���P�[�V�����V�K�쐬�ƃR���|�[�l���g�z�u - ���߂������
  * http://konbu13.hatenablog.com/entry/2013/12/27/163202

���l�F

* ���߂�Swing Designer�Ńt���[�����쐬���A���C�A�E�g�� `MigLayout` ��I�������Ƃ���AEclipse �v���W�F�N�g������ `miglayout15-swing.jar` �� `miglayout-src.zip` ��������DL����AEclipse �v���W�F�N�g�� Java Build Path �Ƀ��C�u�����Ƃ��Ď����Œǉ�����Ă��܂����B
* Swing Designer ���͂�ł������߂��AEclipse �N�����͂����̃t�@�C���͊��S�ɂ͍폜�ł��Ȃ������B
* �����̂��߁A��UEclipse���I�������ăt�@�C�����폜������AEclipse�v���W�F�N�g �v���p�e�B��Java Build Path ���炱����jar�����Ƃō폜�����肵���B
* ����ɁA���̂܂܂ł� `MigLayout` �֘A��import�ŃG���[�ƂȂ邽�߁Apom.xml �ɓ����� `com.miglayout:miglayout-swing:4.2` ��ǉ����ăR���p�C���G���[�����������B

#### `MigLayout` �Ŏg�p���Ă��� `miglayout-swing` �ɂ���(2017-09-27���_)�F

* http://www.miglayout.com/
* ���Ƃ��� http://www.migcalendar.com/ �Ƃ���Java��GUI�̃J�����_�[�R���|�[�l���g���J�����Ă����Ђ̐��i�B
* ���C�Z���X�Ƃ��Ă�BSD/GPL�̃f���A�����C�Z���X�Ȃ̂ŁA����̗��p�ɂ͖��Ȃ��Ɣ��f�����B(2017-09-27)
