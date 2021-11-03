# A Text2Speech services using Edge tts engine.

Written in java, jdk >= 11

Support Chinese/English

## Usage

```shell
Usage: <main class> [options]
  Options:
    --help, -h, -help
      Show Help message
    -save, --save, -s
      Where to save the wav file
      Default: ./BoyNextDoor.wav
    -text, --text
      Text to be speech
      Default: Boy next door
```

Example: 
```shell
export "JAVA_HOME=/usr/lib/jvm/java-11-openjdk"
export "PATH=$JAVA_HOME/bin:$PATH"
$ java -jar projecttts.jar --text "Hello boy !" --save ./test.wav
```
Download [Test audio file](./test.wav)

## Build
```
fish$ set -x JAVA_HOME /usr/lib/jvm/java-11-openjdk
fish$ set -x PATH $JAVA_HOME/bin $PATH
fish$ mvn assembly:single
```


## Contribution

- Send issue. 
- Send PR with doc comment. 
- No Complex code please.
- Using Intellij IDEA Community
