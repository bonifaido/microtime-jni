# Taken from JNA's Makefile
OS=$(shell uname | sed -e 's/CYGWIN.*/win32/g' \
	                -e 's/MINGW32.*/win32/g' \
                        -e 's/SunOS.*/solaris/g' \
                        -e 's/NetBSD/netbsd/g' \
                        -e 's/GNU\/kFreeBSD/kfreebsd/g' \
                        -e 's/FreeBSD/freebsd/g' \
                        -e 's/OpenBSD/openbsd/g' \
                        -e 's/Darwin.*/darwin/g' \
                        -e 's/AIX.*/aix/g' \
                        -e 's/Linux.*/linux/g')

ARCH=$(shell uname -m)

LD_FLAGS=-shared

ifeq ($(OS),linux)
PCFLAGS+=-fPIC
LIBSFX=.so
endif

ifeq ($(OS),darwin)
LIBSFX=.dylib
endif

BUILD=target

SOURCES=src/main/c/microtime.c

JAVA_INCLUDES=-I"$(JAVA_HOME)/include" \
              -I"$(JAVA_HOME)/include/$(OS)"

JAVAH_INCLUDES=-I$(BUILD)

JAR=microtime-1.0-SNAPSHOT.jar

LIBRARY=libmicrotime$(LIBSFX)
LIBRARY_PATH=$(OS)/$(ARCH)/$(LIBRARY)

# makes platform independent parts
java:
	mvn package
	javah -d $(BUILD) -classpath $(BUILD)/$(JAR) microtime.MicroTime

# adds platform sepcific parts
native:
	mkdir -p $(BUILD)/$(OS)/$(ARCH)
	gcc $(PCFLAGS) $(LD_FLAGS) $(SOURCES) $(JAVAH_INCLUDES) $(JAVA_INCLUDES) -o $(BUILD)/$(LIBRARY_PATH)
	jar uf $(BUILD)/$(JAR) -C $(BUILD) $(LIBRARY_PATH)

test:
	jar tvf $(BUILD)/$(JAR)
	java -cp $(BUILD)/$(JAR) microtime.MicroTime

clean:
	rm -rf $(BUILD)

release:
	mvn install
