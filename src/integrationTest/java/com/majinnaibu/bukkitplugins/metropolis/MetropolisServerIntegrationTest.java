package com.majinnaibu.bukkitplugins.metropolis;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.UUID;

import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.Test;

class MetropolisServerIntegrationTest {
	@Test
	void runningPaperLoadsMetropolis() throws IOException {
		String installedPlugins;
		try (RconClient rcon = connectToConfiguredServer()) {
			installedPlugins = rcon.execute("plugins");
		}
		assertTrue(installedPlugins.contains("Metropolis"), installedPlugins);
	}

	@Test
	void plotReservationIsPersistedByTheLoadedWorldGuardPlugin() throws IOException {
		String regionId = "metropolis-it-" + UUID.randomUUID().toString().substring(0, 8);
		try (RconClient rcon = connectToConfiguredServer()) {
			String response = rcon.execute("metropolis-plot-reserve " + regionId + " 100 60 100 105 70 105");
			assertTrue(response.contains("Reserved plot region " + regionId), response);
		}

		Path serverDirectory = Path.of(requiredEnvironment("METROPOLIS_SERVER_DIR"));
		String worldName = System.getenv().getOrDefault("METROPOLIS_WORLD", "world");
		Path regionsFile = serverDirectory.resolve("plugins/WorldGuard/worlds")
				.resolve(worldName)
				.resolve("regions.yml");
		YamlConfiguration regions = YamlConfiguration.loadConfiguration(regionsFile.toFile());

		assertTrue(regions.contains("regions." + regionId), "WorldGuard did not persist region " + regionId);
	}

	private static RconClient connectToConfiguredServer() throws IOException {
		String host = requiredEnvironment("METROPOLIS_RCON_HOST");
		int port = Integer.parseInt(requiredEnvironment("METROPOLIS_RCON_PORT"));
		String password = requiredEnvironment("METROPOLIS_RCON_PASSWORD");
		return new RconClient(host, port, password);
	}

	private static String requiredEnvironment(String name) {
		String value = System.getenv(name);
		if (value == null || value.isBlank()) {
			throw new IllegalStateException("Set " + name + " to run integrationTest.");
		}
		return value;
	}

	private static final class RconClient implements AutoCloseable {
		private final Socket socket = new Socket();
		private final DataInputStream input;
		private final DataOutputStream output;
		private int requestId = 1;

		private RconClient(String host, int port, String password) throws IOException {
			socket.connect(new InetSocketAddress(host, port), 5000);
			socket.setSoTimeout(5000);
			input = new DataInputStream(socket.getInputStream());
			output = new DataOutputStream(socket.getOutputStream());
			int authId = requestId++;
			Packet response = send(authId, 3, password);
			if (response.id != authId) {
				throw new IOException("RCON authentication failed.");
			}
		}

		private String execute(String command) throws IOException {
			return send(requestId++, 2, command).payload;
		}

		private Packet send(int id, int type, String payload) throws IOException {
			byte[] data = payload.getBytes(StandardCharsets.UTF_8);
			writeLittleEndianInt(data.length + 10);
			writeLittleEndianInt(id);
			writeLittleEndianInt(type);
			output.write(data);
			output.writeByte(0);
			output.writeByte(0);
			output.flush();

			int length = readLittleEndianInt();
			byte[] packet = input.readNBytes(length);
			if (packet.length != length || length < 10) {
				throw new IOException("Received an invalid RCON packet.");
			}
			int responseId = littleEndianInt(packet, 0);
			String response = new String(packet, 8, length - 10, StandardCharsets.UTF_8);
			return new Packet(responseId, response);
		}

		private void writeLittleEndianInt(int value) throws IOException {
			output.writeInt(Integer.reverseBytes(value));
		}

		private int readLittleEndianInt() throws IOException {
			return Integer.reverseBytes(input.readInt());
		}

		private static int littleEndianInt(byte[] bytes, int offset) {
			return (bytes[offset] & 0xff)
					| ((bytes[offset + 1] & 0xff) << 8)
					| ((bytes[offset + 2] & 0xff) << 16)
					| (bytes[offset + 3] << 24);
		}

		@Override
		public void close() throws IOException {
			socket.close();
		}
	}

	private record Packet(int id, String payload) {}
}