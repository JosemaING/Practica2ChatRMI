package es.ubu.lsi.client;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Scanner;

import es.ubu.lsi.common.ChatMessage;
import es.ubu.lsi.server.ChatServer;

/**
 * Clase ChatClientStarter que inicia un nuevo cliente.
 * 
 * @author Jose Maria Santos
 * @version 1.0
 */
public class ChatClientStarter {

	/** Nickname del cliente. */
	private String nickname;

	/** Máquina del cliente. */
	private String hostCliente;

	/**
	 * Constructor de la clase ChatClientStarter que representa a un cliente que
	 * puede enviar y recibir mensajes de un servidor.
	 *
	 * @param args lista de parámetros para el cliente. Primera posición del array,
	 *             NickName del usuario.
	 */
	public ChatClientStarter(String[] args) {

		this.nickname = args[0];
		// De forma predeterminada nos conectamos al localhost
		this.hostCliente = "localhost";
		// Iniciamos el host del cliente
		start();

	}

	/**
	 * Inicia el chat y los mensajes entre clientes a través del servidor.
	 *
	 * @see ChatServer
	 * @see ChatClientImpl
	 */
	public void start() {
		// Inicio bloque try...
		try {
			String mensaje;
			// Creamos un objeto chatClient con el nickName
			ChatClientImpl chatClient = new ChatClientImpl(nickname);
			UnicastRemoteObject.exportObject(chatClient, 0);
			Registry registry = LocateRegistry.getRegistry(hostCliente);
			ChatServer servidor = (ChatServer) registry.lookup("/servidor");
			servidor.checkIn(chatClient);
			try (Scanner scanner = new Scanner(System.in)) {
				// Iniciamos un bucle para la conversación
				System.out.println("\n¡Hola, " + nickname + "! Teclado activo, esperando mensajes... ");
				System.out.println("------------------------------------------- ");
				do {
					mensaje = scanner.nextLine();
					// Si el mensaje igual a logout salimos del chat
					if (mensaje.equals("logout")) {
						servidor.logout(chatClient);
						System.exit(0);
						// Si el mensaje igual a shutdown cerramos el servidor
					} else if (mensaje.equals("shutdown")) {
						servidor.shutdown(chatClient);
						System.exit(0);
						// Si es distinto a login o shutdown, publicamos el mensaje con el nickName e id
						// del cliente
					} else {
						ChatMessage message = new ChatMessage(chatClient.getId(), nickname, mensaje);
						servidor.publish(message);
					}
				} while (true);
			}
		} catch (RemoteException | NotBoundException e) {
			// Muestra el mensaje de la excepción que ha ocurrido
			System.out.println(e.getMessage());
			System.exit(0);
		}

	}
}
