YawkatSnap
==========

Java Windows FTP screenshots, like ScreenSnapr

What this does
--------------

This program allows you to screenshot anything and upload it to your FTP server directly. You can screenshot a part of your desktop using `Ctrl+1` or directly upload an image or file from your clipboard using `Ctrl+2`. The filename for the image can be configured in Regex form, for example `[0-9a-zA-z]{4}` to create a random, 4-character string. Then, this program copies a link to the image to your clipboard.

How to build
------------

1. Install [maven](http://maven.apache.org/)
2. [Install the Xeger library into maven](http://www.hrupin.com/2012/01/how-to-use-xeger-lib-with-maven)
3. Build the source code: `mvn build clean compile assembly:single`
4. Download the [JIntellitype library](https://code.google.com/p/jintellitype/downloads/detail?name=jintellitype-1.3.7-dist.zip&can=2&q=)
5. Unzip JIntellitype
6. Add the `JIntellitype.dll` and `JIntellitype64.dll` files from the downloaded zip file to the `com/melloware/jintellitype` directory inside the .jar file you built in step 3 (For example using 7zip)
7. Copy the jar file from step 6 into your desired directory
8. Create a file called `config.properties` in the same directory and fill it with this code:
        
        save.type=ftp
        save.ftp.host=
        save.ftp.port=
        save.ftp.username=
        save.ftp.password=
        save.ftp.directory=
        save.ftp.filetype=
        save.ftp.filename=
        idgen.type=regex
        idgen.regex.regex=
        clipboard=

    Fill in these entries:
    * `save.ftp.host` Hostname of your FTP server
    * `save.ftp.port` Port of your FTP server, usually `21`
    * `save.ftp.username` Username for your FTP server
    * `save.ftp.password` password for your FTP server
    * `save.ftp.directory` upload directory, should start and end with a forward slash, for example `/images/`
    * `save.ftp.filetype` the image file type, such as `PNG` (recommended), `GIF` or `JPG`
    * `save.ftp.filename` the image file name. `%id` will be replaced with the generated id. for PNG this could be `%id.png`
    * `idgen.regex.regex` the regular expression for the image ID, for example `[0-9a-zA-Z]{4}`
    * `clipboard` the clipboard link to the image. Once again, `%id` will be replaced by the image id. This might be `http://example.com/images/%id.png` or `http://example.com/image.php?id=%id`
9. Launch the program by double-clicking the jar file
