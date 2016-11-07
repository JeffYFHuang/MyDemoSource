package storm.cookbook;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import junit.framework.TestCase;
import kafka.api.FetchRequest;
import kafka.api.FetchRequestBuilder;
import kafka.javaapi.FetchResponse;
import kafka.javaapi.consumer.SimpleConsumer;
import kafka.javaapi.message.ByteBufferMessageSet;
import kafka.javaapi.producer.Producer;
import kafka.message.MessageAndOffset;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.junit.Test;

import org.apache.storm.utils.Utils;

public class AcceptanceTest extends TestCase {

	private static Map<String, Object[]> testData = new HashMap<String, Object[]>();

	static {
		testData.put("d2e1e3d1", new Object[] {
				"Hub2",
				new double[] { 0.93, 0.84, 0.63, 0.79, 0.96, 0.71, 0.69, 0.68,
						0.89, 0.77 } });
		testData.put("d2e2b317", new Object[] {
				"Hub2",
				new double[] { 0.71, 0.69, 0.50, 0.54, 0.38, 0.75, 0.46, 0.02,
						0.73, 0.40 } });
		testData.put("d2e2b74f", new Object[] {
				"Hub1",
				new double[] { 1.39, 1.32, -0.26, -0.13, -0.73, -0.20, 0.69,
						-0.33, -0.62, 0.24 } });
		testData.put("d2e2be00", new Object[] {
				"Hub1",
				new double[] { 1.26, 1.69, 1.24, 1.66, 1.46, 1.40, 1.51, 1.74,
						1.64, 1.44 } });
		testData.put("d2e2c507", new Object[] {
				"Hub2",
				new double[] { 0.68, 0.83, 1.02, 0.49, 0.70, 0.72, 0.29, 0.51,
						0.27, 0.46 } });
		testData.put("d2e2cb0a", new Object[] {
				"Hub1",
				new double[] { 1.51, 1.65, 1.22, 1.46, 1.28, 1.31, 1.79, 1.66,
						1.55, 1.83 } });
		testData.put("d2e2d102", new Object[] {
				"Hub1",
				new double[] { 1.18, 1.31, 1.82, 1.54, 1.36, 1.29, 1.98, 1.15,
						1.57, 1.37 } });
		testData.put("d2e2d6fd", new Object[] {
				"Hub1",
				new double[] { 1.58, 1.52, 0.97, 0.97, 1.16, 1.67, 1.32, 1.32,
						1.25, 2.06 } });
		testData.put("d2e2def5", new Object[] {
				"Hub1",
				new double[] { 1.78, 1.76, 1.58, 1.55, 1.28, 1.51, 1.46, 1.74,
						1.48, 1.66 } });
		testData.put("d2e2e694", new Object[] {
				"Hub1",
				new double[] { 1.97, 1.57, 1.75, 1.48, 1.33, 1.41, 1.46, 1.46,
						1.58, 1.49 } });
		testData.put("d2e2eab8", new Object[] {
				"Hub2",
				new double[] { 0.67, 0.43, 0.71, 0.99, 0.48, 0.58, 0.08, 0.67,
						0.54, 0.69 } });
		testData.put("d2e2ee8c", new Object[] {
				"Hub1",
				new double[] { 0.21, 1.44, 0.92, -0.58, 0.34, 1.30, -0.52,
						0.76, -0.29, 0.66 } });
		testData.put("d2e2f25e", new Object[] {
				"Hub1",
				new double[] { 0.51, -0.55, -0.28, -0.16, 0.05, -0.98, 0.22,
						1.78, -0.53, 0.07 } });
		testData.put("d2e2f621", new Object[] {
				"Hub1",
				new double[] { 1.35, 1.29, 1.30, 1.28, 1.85, 0.90, 1.36, 1.71,
						1.67, 1.60 } });
		testData.put("d2e2f9eb", new Object[] {
				"Hub1",
				new double[] { 1.15, 1.89, 1.25, 1.67, 1.13, 0.90, 1.20, 1.41,
						1.35, 1.97 } });
		testData.put("d2e2fda8", new Object[] {
				"Hub1",
				new double[] { 1.66, 1.68, 1.51, 1.68, 1.84, 1.16, 1.31, 1.23,
						1.52, 1.42 } });
		testData.put("d2e30173", new Object[] {
				"Hub1",
				new double[] { 1.48, 1.79, 1.32, 1.84, 1.32, 1.20, 1.60, 1.35,
						1.47, 1.77 } });
		testData.put("d2e30533", new Object[] {
				"Hub1",
				new double[] { 1.38, 1.40, 1.37, 1.21, 1.55, 1.46, 1.93, 1.60,
						1.16, 1.86 } });
		testData.put("d2e30a3d", new Object[] {
				"Hub1",
				new double[] { 2.01, 1.25, 1.18, 1.79, 1.24, 1.47, 1.72, 1.28,
						1.35, 1.29 } });
		testData.put("d2e31145", new Object[] {
				"Hub1",
				new double[] { 1.68, 1.37, 1.19, 1.53, 1.60, 1.30, 1.61, 1.41,
						2.12, 1.83 } });
		testData.put("d2e3154c", new Object[] {
				"Hub1",
				new double[] { 0.28, -0.57, 0.63, -0.49, 1.00, 0.18, -0.35,
						0.09, -0.18, 1.80 } });
		testData.put("d2e31921", new Object[] {
				"Hub1",
				new double[] { 1.19, 0.43, 0.43, -0.03, -0.06, -0.15, 0.71,
						-0.11, -0.89, 1.31 } });
		testData.put("d2e31ceb", new Object[] {
				"Hub1",
				new double[] { 0.74, -0.24, 0.81, 0.14, 1.88, 1.17, -0.08,
						0.62, -0.51, 0.38 } });
		testData.put("d2e320dc", new Object[] {
				"Hub2",
				new double[] { 0.67, 1.02, 0.80, 0.61, 0.61, 0.92, 0.95, 0.90,
						0.80, 0.39 } });
		testData.put("d2e32780", new Object[] {
				"Hub1",
				new double[] { 2.08, 1.59, 1.74, 1.87, 1.78, 1.36, 1.39, 1.38,
						1.47, 1.31 } });
		testData.put("d2e32dc0", new Object[] {
				"Hub1",
				new double[] { 1.64, 1.75, 1.36, 1.51, 1.68, 1.21, 1.29, 1.25,
						1.18, 1.80 } });
		testData.put("d2e331c7", new Object[] {
				"Hub2",
				new double[] { 0.70, 0.55, 0.52, 0.56, 0.75, 0.50, 0.45, 0.89,
						0.65, 0.35 } });
		testData.put("d2e33680", new Object[] {
				"Hub1",
				new double[] { 1.43, 1.47, 1.28, 1.56, 1.57, 1.77, 1.69, 1.43,
						1.51, 1.61 } });
		testData.put("d2e33cd4", new Object[] {
				"Hub1",
				new double[] { 1.66, 1.78, 0.99, 1.42, 1.50, 1.78, 1.44, 1.12,
						1.56, 1.59 } });
		testData.put("d2e340e3", new Object[] {
				"Hub1",
				new double[] { 1.10, 1.77, 1.52, 1.30, 1.58, 1.64, 1.73, 1.70,
						1.58, 1.47 } });
		testData.put("d2e344ae", new Object[] {
				"Hub1",
				new double[] { 1.48, 1.61, 1.77, 1.74, 1.43, 1.49, 1.62, 1.40,
						1.39, 1.39 } });
		testData.put("d2e34875", new Object[] {
				"Hub2",
				new double[] { 0.32, 0.31, 0.49, 0.45, 0.53, 0.35, 0.71, 0.76,
						0.49, 0.51 } });
		testData.put("d2e34c4a", new Object[] {
				"Hub1",
				new double[] { 1.78, 1.45, 1.72, 1.03, 1.50, 1.21, 1.44, 1.65,
						1.15, 1.26 } });
		testData.put("d2e352c7", new Object[] {
				"Hub1",
				new double[] { 1.71, 1.21, 1.85, 1.59, 1.64, 1.51, 1.72, 0.56,
						1.42, 1.63 } });
		testData.put("d2e359b0", new Object[] {
				"Hub1",
				new double[] { 1.20, 1.33, 1.39, 1.86, 1.42, 1.42, 1.29, 1.52,
						1.53, 1.76 } });
		testData.put("d2e360c2", new Object[] {
				"Hub1",
				new double[] { 1.78, 1.79, 1.43, 1.54, 1.23, 0.87, 1.60, 1.49,
						2.03, 1.36 } });
		testData.put("d2e367f3", new Object[] {
				"Hub2",
				new double[] { 0.64, 0.77, 0.59, 0.33, 0.75, 0.97, 0.64, 0.58,
						0.76, 0.53 } });
		testData.put("d2e36f2e", new Object[] {
				"Hub2",
				new double[] { 0.67, 0.84, 0.92, 0.40, 0.43, 0.75, 0.62, 0.68,
						0.81, 0.81 } });
		testData.put("d2e37668", new Object[] {
				"Hub1",
				new double[] { 1.27, 1.22, 1.32, 1.71, 1.79, 1.53, 1.26, 1.43,
						1.63, 1.34 } });
		testData.put("d2e37ccf", new Object[] {
				"Hub1",
				new double[] { 1.24, 1.65, 1.56, 1.48, 1.14, 1.58, 1.80, 1.80,
						1.70, 1.35 } });
		testData.put("d2e382ca", new Object[] {
				"Hub1",
				new double[] { 1.70, 1.68, 1.25, 0.98, 1.05, 1.38, 1.52, 1.64,
						1.54, 1.30 } });
		testData.put("d2e388b0", new Object[] {
				"Hub1",
				new double[] { 1.45, 1.53, 1.50, 1.99, 1.14, 1.58, 1.62, 1.91,
						1.45, 1.46 } });
		testData.put("d2e38e94", new Object[] {
				"Hub1",
				new double[] { 1.60, 1.50, 1.35, 0.90, 1.77, 1.15, 1.41, 1.52,
						1.76, 1.17 } });
		testData.put("d2e39542", new Object[] {
				"Hub2",
				new double[] { 0.96, 0.48, 0.84, 0.31, 0.63, 0.35, 0.96, 0.31,
						0.76, 0.80 } });
		testData.put("d2e39c38", new Object[] {
				"Hub1",
				new double[] { -0.26, -0.10, -0.46, -0.23, 0.33, -0.93, 0.25,
						0.55, 1.67, -0.06 } });
		testData.put("d2e3a335", new Object[] {
				"Hub1",
				new double[] { 1.62, 1.38, 1.87, 1.28, 1.89, 1.23, 1.77, 1.15,
						1.73, 1.78 } });
		testData.put("d2e3a9ee", new Object[] {
				"Hub1",
				new double[] { 1.47, 1.51, 1.15, 1.48, 1.50, 1.13, 1.58, 1.43,
						1.34, 1.39 } });
		testData.put("d2e3b0cc", new Object[] {
				"Hub2",
				new double[] { 0.43, 0.49, 0.60, 0.26, 0.74, 0.81, 0.55, 0.28,
						0.42, 0.43 } });
		testData.put("d2e3b7a3", new Object[] {
				"Hub1",
				new double[] { 1.90, 1.06, 1.24, 1.39, 1.58, 1.67, 1.63, 1.40,
						1.35, 1.52 } });
		testData.put("d2e3bef3", new Object[] {
				"Hub1",
				new double[] { 1.81, 1.54, 1.98, 1.09, 1.67, 1.47, 0.98, 1.19,
						0.94, 1.65 } });
		testData.put("d2e3c942", new Object[] {
				"Hub1",
				new double[] { 1.59, 1.77, 1.03, 1.68, 1.78, 1.76, 1.45, 1.53,
						1.18, 1.44 } });
		testData.put("d2e3d0a3", new Object[] {
				"Hub1",
				new double[] { 1.30, 1.52, 1.94, 1.56, 1.53, 1.85, 1.76, 1.35,
						1.86, 1.30 } });
		testData.put("d2e3d7b5", new Object[] {
				"Hub1",
				new double[] { 1.44, 1.50, 1.59, 1.30, 1.39, 0.99, 2.01, 1.28,
						1.42, 1.62 } });
		testData.put("d2e3dedc", new Object[] {
				"Hub1",
				new double[] { 1.63, 1.37, 1.91, 1.91, 1.75, 1.27, 0.98, 1.56,
						1.57, 1.64 } });
		testData.put("d2e3e5ee", new Object[] {
				"Hub2",
				new double[] { 0.31, 0.56, 0.56, 0.82, 0.66, 0.75, 0.69, 0.40,
						0.47, 0.18 } });
		testData.put("d2e3ed28", new Object[] {
				"Hub1",
				new double[] { 0.41, -1.13, 0.12, 0.20, 0.49, 0.32, 1.04, 0.46,
						-0.76, 0.94 } });
		testData.put("d2e3f494", new Object[] {
				"Hub1",
				new double[] { 1.69, 1.74, 1.70, 1.35, 1.73, 1.72, 1.37, 1.33,
						1.82, 1.65 } });
		testData.put("d2e3fbe1", new Object[] {
				"Hub1",
				new double[] { 2.00, 1.86, 1.50, 1.64, 1.35, 1.37, 1.19, 1.29,
						1.67, 1.97 } });
		testData.put("d2e40326", new Object[] {
				"Hub1",
				new double[] { 1.35, 1.33, 1.31, 1.67, 1.78, 1.58, 1.71, 1.61,
						1.45, 1.53 } });
		testData.put("d2e40a40", new Object[] {
				"Hub1",
				new double[] { 1.58, 1.76, 1.47, 1.37, 1.34, 1.64, 1.50, 1.49,
						1.45, 1.78 } });
		testData.put("d2e411e8", new Object[] {
				"Hub1",
				new double[] { 1.37, 1.70, 0.93, 1.45, 1.53, 1.33, 1.15, 1.12,
						1.77, 1.25 } });
		testData.put("d2e418c0", new Object[] {
				"Hub1",
				new double[] { 1.59, 1.42, 1.90, 1.66, 1.51, 1.04, 1.28, 1.04,
						1.56, 1.43 } });
		testData.put("d2e41f3d", new Object[] {
				"Hub2",
				new double[] { 0.84, 0.75, 0.76, 0.92, 1.07, 0.87, 1.00, 0.52,
						0.43, 0.53 } });
		testData.put("d2e425a3", new Object[] {
				"Hub1",
				new double[] { 0.10, 1.64, 1.01, 0.10, 0.09, -0.87, 0.04, 0.76,
						-0.43, -1.06 } });
		testData.put("d2e42c17", new Object[] {
				"Hub1",
				new double[] { 1.56, 1.32, 1.54, 1.48, 1.69, 1.31, 1.81, 1.31,
						1.41, 1.47 } });
		testData.put("d2e4342b", new Object[] {
				"Hub2",
				new double[] { 0.78, 1.18, 0.48, 0.80, 0.69, 0.65, 0.77, 0.48,
						0.67, 0.67 } });
		testData.put("d2e43ac7", new Object[] {
				"Hub1",
				new double[] { 0.93, 0.35, -0.29, 0.63, 1.20, -0.82, 0.88,
						-0.19, 0.26, 0.59 } });
		testData.put("d2e4419c", new Object[] {
				"Hub1",
				new double[] { 2.33, 1.92, 1.34, 1.41, 1.14, 1.56, 1.77, 1.41,
						1.68, 1.37 } });
		testData.put("d2e44854", new Object[] {
				"Hub1",
				new double[] { 1.76, 1.67, 1.34, 1.18, 1.39, 1.38, 1.85, 1.27,
						1.26, 1.59 } });
		testData.put("d2e44ecf", new Object[] {
				"Hub1",
				new double[] { 1.20, 0.93, 1.40, 1.74, 1.62, 1.06, 1.60, 1.18,
						1.16, 1.57 } });
		testData.put("d2e4554c", new Object[] {
				"Hub1",
				new double[] { 1.15, 0.39, -0.10, 0.21, 0.33, -0.35, 0.26,
						-0.39, 1.69, -1.08 } });
		testData.put("d2e45bbd", new Object[] {
				"Hub1",
				new double[] { 1.26, 1.20, 1.89, 1.32, 1.50, 1.65, 1.51, 1.39,
						1.48, 1.42 } });
		testData.put("d2e4623a", new Object[] {
				"Hub1",
				new double[] { 1.42, 1.86, 1.76, 1.63, 1.58, 1.29, 1.43, 1.22,
						1.85, 1.35 } });
		testData.put("d2e468c0", new Object[] {
				"Hub1",
				new double[] { -0.30, -0.38, 1.53, -1.24, 0.31, -0.48, 0.92,
						0.01, -0.38, 1.09 } });
		testData.put("d2e46f94", new Object[] {
				"Hub1",
				new double[] { 1.23, 1.49, 1.53, 1.32, 1.08, 1.49, 1.36, 1.29,
						1.59, 1.35 } });
		testData.put("d2e476ee", new Object[] {
				"Hub1",
				new double[] { 1.72, 1.01, 1.98, 1.26, 1.31, 1.30, 1.42, 1.68,
						1.52, 1.61 } });
		testData.put("d2e47e63", new Object[] {
				"Hub1",
				new double[] { 1.81, 1.08, 0.83, 1.32, 1.96, 1.42, 1.64, 1.59,
						1.97, 1.74 } });
		testData.put("d2e4859e", new Object[] {
				"Hub1",
				new double[] { 1.28, 1.62, 1.52, 1.07, 1.55, 1.33, 2.07, 1.42,
						1.24, 1.64 } });
		testData.put("d2e48cb8", new Object[] {
				"Hub1",
				new double[] { 1.21, 1.33, 1.45, 1.34, 1.38, 1.67, 1.56, 1.45,
						1.86, 1.27 } });
		testData.put("d2e4937a", new Object[] {
				"Hub1",
				new double[] { 1.39, 1.65, 1.41, 1.51, 1.39, 1.78, 1.13, 1.87,
						1.28, 1.58 } });
		testData.put("d2e49a00", new Object[] {
				"Hub1",
				new double[] { 0.03, -1.32, -0.63, 0.03, -0.22, 0.77, 1.21,
						0.05, -0.72, 0.17 } });
		testData.put("d2e4a080", new Object[] {
				"Hub1",
				new double[] { -0.02, -0.29, 1.32, 0.45, 0.50, -0.68, 0.53,
						1.02, 1.89, 0.95 } });
		testData.put("d2e4a811", new Object[] {
				"Hub1",
				new double[] { 1.25, 1.64, 1.44, 1.71, 1.41, 1.68, 1.53, 1.49,
						1.52, 1.28 } });
		testData.put("d2e4ae33", new Object[] {
				"Hub1",
				new double[] { 1.78, 1.51, 1.60, 1.45, 1.81, 1.82, 1.78, 1.63,
						1.54, 1.49 } });
		testData.put("d2e4b42e", new Object[] {
				"Hub1",
				new double[] { -0.37, -0.15, 0.49, 0.36, 1.42, 1.17, 0.73,
						0.66, -0.22, -1.81 } });
		testData.put("d2e4bbe1", new Object[] {
				"Hub2",
				new double[] { 0.96, 1.12, 0.59, 0.96, 0.62, 0.93, 0.80, 0.74,
						1.10, 0.35 } });
		testData.put("d2e4c266", new Object[] {
				"Hub1",
				new double[] { 1.65, 1.32, 1.47, 1.44, 1.54, 0.86, 1.26, 0.99,
						1.58, 1.16 } });
		testData.put("d2e4c8cc", new Object[] {
				"Hub1",
				new double[] { 1.71, 1.17, 1.46, 1.52, 2.00, 1.08, 1.56, 1.81,
						1.67, 1.61 } });
		testData.put("d2e4cf2b", new Object[] {
				"Hub2",
				new double[] { 0.67, 0.46, 0.45, 0.63, 0.71, 0.64, 0.44, 0.59,
						0.37, 0.70 } });
		testData.put("d2e4d594", new Object[] {
				"Hub1",
				new double[] { -0.65, -1.00, -0.51, -0.19, -0.18, -0.07, 0.33,
						-0.21, 1.56, -1.75 } });
		testData.put("d2e4dc6b", new Object[] {
				"Hub1",
				new double[] { 1.08, 1.99, 1.64, 1.81, 1.82, 1.58, 1.49, 1.45,
						1.85, 1.30 } });
		testData.put("d2e4e340", new Object[] {
				"Hub1",
				new double[] { 1.39, -0.29, 0.20, 0.79, -0.06, -1.82, 1.12,
						-0.74, -1.07, 0.62 } });
		testData.put("d2e4e9ba", new Object[] {
				"Hub1",
				new double[] { -1.33, 0.40, -0.35, 0.58, 1.41, 0.33, 1.10,
						-0.73, -0.78, 1.17 } });
		testData.put("d2e4f019", new Object[] {
				"Hub1",
				new double[] { -0.95, 0.17, 0.70, -0.26, 0.22, -0.45, 0.42,
						0.86, 0.67, 0.18 } });
		testData.put("d2e4f6a8", new Object[] {
				"Hub1",
				new double[] { 1.80, 1.13, 1.21, 1.50, 2.09, 1.51, 1.39, 2.01,
						1.14, 1.58 } });
		testData.put("d2e4fd11", new Object[] {
				"Hub1",
				new double[] { 0.27, 0.26, 0.68, 0.36, 0.03, 0.28, 0.51, 0.68,
						-0.18, 0.07 } });
		testData.put("d2e50370", new Object[] {
				"Hub1",
				new double[] { 1.43, 1.41, 1.53, 2.00, 1.66, 1.38, 1.41, 1.57,
						1.44, 1.55 } });
		testData.put("d2e509eb", new Object[] {
				"Hub1",
				new double[] { 1.72, 1.70, 1.18, 1.50, 1.63, 1.28, 1.34, 1.26,
						1.28, 1.68 } });
		testData.put("d2e510ca", new Object[] {
				"Hub1",
				new double[] { 1.65, 1.66, 1.42, 1.38, 1.55, 1.47, 1.13, 1.21,
						1.74, 1.67 } });
		testData.put("d2e517e8", new Object[] {
				"Hub2",
				new double[] { 0.85, 0.35, 0.56, 0.87, 0.73, 0.74, 0.18, 0.49,
						0.57, 0.50 } });
	}

	private static String kafkaHost;
	private Producer producer;

	private String createInput(double[] inputs, String orderId) {
		JSONArray array = new JSONArray();
		for (int i = 0; i < inputs.length; i++) {
			array.add(Double.toString(inputs[i]));
		}
		array.add(orderId);
		return array.toJSONString();
	}

	private static long offset = 0;

	@Test
	public void testTopology() throws InterruptedException {
		kafkaHost = System.getProperty("zk.host", "172.18.161.250");
		Properties props = new Properties();
		props.put("zk.connect", kafkaHost + ":" + Integer.toString(2181));
		props.put("serializer.class", "kafka.serializer.StringEncoder");
		props.put("zk.connectiontimeout.ms", "1000000");
		props.put("groupid", "default_group");
		props.put("auto.commit", "true");
		ProducerConfig config = new ProducerConfig(props);
		producer = new Producer<String, String>(config);
		
		SimpleConsumer consumer = new SimpleConsumer(kafkaHost, 9092, 10000, 1024000, "test");
		
		//warm up message
		double[] inputs = (double[]) testData.values().iterator().next()[1];
		String input = createInput(inputs, "testOrder");
		KeyedMessage data = new KeyedMessage<String, String>(
				"orders", input);
		producer.send(data);
		
		FetchRequest fetchRequest = new FetchRequestBuilder()
        .clientId("test")
        .addFetch("order-output", 0, offset, 1000000)
        .build();

		FetchResponse fetchResponse = consumer.fetch(fetchRequest);
		int warmUpAttempts = 0;
		boolean warm = false;
		while(!warm){
			Thread.sleep(2000);
			for (MessageAndOffset msg : fetchResponse.messageSet("order-output", 0)) {
				offset = msg.offset();
				warm = true;
			}
			warmUpAttempts++;
			if(warmUpAttempts > 20)
				fail("Too many tries trying to warm up");
		}
		
		
		for (String orderId : testData.keySet()) {
			inputs = (double[]) testData.get(orderId)[1];
			input = createInput(inputs, orderId);
			data = new KeyedMessage<String, String>(
					"orders", input);
			producer.send(data);
		}

		Thread.sleep(45000);

		int count = 0;
		int tested = 0;
		int errorCount = 0;
		while ((count < 10) && (tested < 100)) {
		    fetchRequest = new FetchRequestBuilder()
			        .clientId("test")
			        .addFetch("order-output", 0, offset, 1000000)
			        .build();
		    fetchResponse = consumer.fetch(fetchRequest);
			for (MessageAndOffset msg : fetchResponse.messageSet("order-output", 0)) {
				String test = new String(Utils.toByteArray(msg.message()
						.payload()));
				offset = msg.offset();
				JSONObject output = (JSONObject) JSONValue.parse(test);
				if (output != null) {
					String tempDispatchTo = (String) output.get("dispatch-to");
					String tempOrderId = (String) output.get("order-id");
					Object[] rhs = testData.get(tempOrderId);
					if (rhs != null) {
						String expected = (String) rhs[0];
						tested++;
						if (!expected.equals(tempDispatchTo))
							errorCount++;
					}
				}
			}
			Thread.sleep(1000);
			count++;
		}

		assertEquals(100, tested);
		assertTrue(errorCount < 3);

	}
}
