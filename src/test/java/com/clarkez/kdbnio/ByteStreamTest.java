package com.clarkez.kdbnio;

public class ByteStreamTest {

	char login[] = { 0x70, 0x65, 0x74, 0x65, 0x72, 0x3a, 0x70, 0x61, 0x73,
			0x73, 0x77, 0x6f, 0x72, 0x64, 0x03, 0x00 };
	char loginResponse[] = { 0x03 };
	char addOneAndOne[] = { 0x01, 0x01, 0x00, 0x00, 0x11, 0x00, 0x00, 0x00, 0x0a,
			0x00, 0x03, 0x00, 0x00, 0x00, 0x31, 0x2b, 0x31 };
	char AnswerIsTwo[] = { 0x01, 0x02, 0x00, 0x00, 0x11, 0x00, 0x00, 0x00, 0xf9,
			0x02, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 };
}
