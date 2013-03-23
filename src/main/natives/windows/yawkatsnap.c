#include <windows.h>
#include <stdlib.h>
#include "at_yawk_snap_YawkatSnapNatives.h"

JNIEXPORT jint JNICALL
Java_at_yawk_snap_YawkatSnapNatives_isKeyPressed(JNIEnv *env, jclass class, jint value)
{
	return GetAsyncKeyState(value);
}

JNIEXPORT jint JNICALL
Java_at_yawk_snap_YawkatSnapNatives_checkAutoStart(JNIEnv *env, jclass class, jstring javapath, jstring programpath, jstring mainclass, jstring programname)
{
	const char* cprogramname = (*env)->GetStringUTFChars(env, programname, 0);
	const int clength = 79 + strlen(cprogramname);
	char* str = NULL;
	str = malloc(clength * sizeof(char));
	strcpy(str, "reg query HKEY_CURRENT_USER\\Software\\Microsoft\\Windows\\CurrentVersion\\Run /v \"");
	strcat(str, cprogramname);
	strcat(str, "\"");
	return system(str);
}

JNIEXPORT jint JNICALL
Java_at_yawk_snap_YawkatSnapNatives_setAutoStart(JNIEnv *env, jclass class, jstring javapath, jstring programpath, jstring mainclass, jstring programname)
{
	const char* cjavapath = (*env)->GetStringUTFChars(env, javapath, 0);
	const char* cprogrampath = (*env)->GetStringUTFChars(env, programpath, 0);
	const char* cprogramname = (*env)->GetStringUTFChars(env, programname, 0);
	const char* cmainclass = (*env)->GetStringUTFChars(env, mainclass, 0);
	const int clength = 100 + strlen(cjavapath) + strlen(cprogrampath) + strlen(cprogramname) + strlen(cmainclass);
	char* str = NULL;
	str = malloc(clength * sizeof(char));
	strcpy(str, "reg add HKEY_CURRENT_USER\\Software\\Microsoft\\Windows\\CurrentVersion\\Run /f /v \"");
	strcat(str, cprogramname);
	strcat(str, "\" /d \"\\\"");
	strcat(str, cjavapath);
	strcat(str, "\\\" -cp \\\"");
	strcat(str, cprogrampath);
	strcat(str, "\\\" ");
	strcat(str, cmainclass);
	strcat(str, "\"");
	return system(str);
}

JNIEXPORT jint JNICALL
Java_at_yawk_snap_YawkatSnapNatives_unsetAutoStart(JNIEnv *env, jclass class, jstring javapath, jstring programpath, jstring mainclass, jstring programname)
{
	const char* cprogramname = (*env)->GetStringUTFChars(env, programname, 0);
	const int clength = 80 + strlen(cprogramname);
	char* str = NULL;
	str = malloc(clength * sizeof(char));
	strcpy(str, "reg delete HKEY_CURRENT_USER\\Software\\Microsoft\\Windows\\CurrentVersion\\Run /f /v \"");
	strcat(str, cprogramname);
	strcat(str, "\"");
	return system(str);
}