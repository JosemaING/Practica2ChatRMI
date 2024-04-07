package es.ubu.lsi.server;

import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

import es.ubu.lsi.client.ChatClient;
import es.ubu.lsi.common.ChatMessage;

/**
 * Clase ChatServerImpl que implementa los métodos de la interfaz ChatServer.
 * 
 * @author Jose Maria Santos
 * @version 1.0
 */

public class ChatServerImpl implements ChatServer {

	/** Contador de clientes que inician en el servidor. */
	private int contador;

	// Mapa para almacenar los clientes por su nickname
	private ConcurrentHashMap<String, ChatClient> clientes = new ConcurrentHashMap<>();

	/** Mapa con los clientes baneados. */
	private ConcurrentHashMap<String, Boolean> clientesBaneados = new ConcurrentHashMap<String, Boolean>();

	/** Formato de fecha. */
	private static SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

	/**
	 * Constructor ChatServerImpl que crea un nuevo servidor. Incializa el contador
	 * de clientes online en 0.
	 * 
	 * @throws RemoteException excepcion si existe problema en la comunicacion
	 * 
	 * @see ChatServer
	 */
	protected ChatServerImpl() throws RemoteException {
		super();
		contador = 0;

	}

	/**
	 * Devuelve la hora exacta en formato texto, este metodo es utilizado para
	 * mostrar por pantalla los mensajes.
	 *
	 * @return fecha en formato texto
	 */
	public String getDateString() {
		return sdf.format(new Date());
	}

	/**
	 * Registra un nuevo usuario en el servidor y se le asigna un Id. Si el cliente
	 * tiene un nickname ya existente se rechaza su conexión al servidor.
	 *
	 * @param client cliente del servidor
	 * @return Id del cliente
	 * @throws RemoteException the remote exception
	 * @see ChatClient
	 */
	public int checkIn(ChatClient client) throws RemoteException {
		// Convertimos el nickname todo en minusculas para compararlos
		String nicknameMinus = client.getNickName().toLowerCase();
		System.out.println("[" + getDateString() + "] " + "CheckIn cliente con nickname: " + nicknameMinus);

		// Verifica si el nickname ya está en uso
		if (clientes.containsKey(nicknameMinus)) {
			System.out.println("[" + getDateString() + "] " + "Nickname duplicado detectado: " + nicknameMinus);
			throw new RemoteException("[" + getDateString() + "] " + "Este nickname ya está en uso.");
		}

		// Cuando se asigna una ID a un cliente, aumentamos el contador
		client.setId(contador);

		// Añadimos el cliente al mapa
		clientes.put(nicknameMinus, client);
		System.out.println("[" + getDateString() + "] " + "Cliente: " + nicknameMinus + " registrado con éxito!");
		contador++;

		System.out.println("[" + getDateString() + "] " + "Clientes online: " + clientes.size());
		return client.getId();
	}

	/**
	 * Indica si un cliente abandona el chat y lo borra de la lista de clientes
	 * conectados.
	 *
	 * @param client cliente que se loguea
	 * @throws RemoteException the remote exception
	 */
	public void logout(ChatClient client) throws RemoteException {

		// Convertimos el nickname todo en minusculas para compararlos
		String nicknameMinus = client.getNickName().toLowerCase();

		// Si el cliente existe lo eliminamos removemos del mapa
		if (clientes.remove(nicknameMinus) != null) {
			System.out.println("[" + getDateString() + "] " + "El cliente " + client.getNickName() + " ha salido.");
			System.out.println("[" + getDateString() + "] " + "Clientes online: " + clientes.size());
		}

		// Mensaje para los clientes
		client.receive(new ChatMessage(-1, "SERVER", "Cerrando sesión..."));
	}

	/**
	 * Envia un mensaje a cada uno de los clientes excepto al cliente que envia
	 * dicho mensaje.
	 *
	 * @param msg mensaje que recibe
	 * @throws RemoteException the remote exception
	 * @see ChatMessage
	 * @see ChatClient
	 */
	public void publish(ChatMessage msg) throws RemoteException {
		// Verifica si el emisor del mensaje está baneado
		if (testBan(msg.getNickname())) {
			return; // Si está baneado, salimos del método y no se procederá a enviar su mensaje
		}

		// Gestionamos el baneo en caso de que hubiera, si no hay no pasará nada
		banClient(msg);

		// Vamos cliente por cliente...
		for (ChatClient cliente : clientes.values()) {
			// Comprobamos que el que envía el mensaje no sea igual que el que lo recibe
			if (!msg.getNickname().equalsIgnoreCase(cliente.getNickName())) {
				try {
					// Enviamos el mensaje a cada uno de los clientes
					cliente.receive(msg);
				} catch (RemoteException e) {
					System.err.println(
							"[" + getDateString() + "] " + "Error al enviar mensaje a " + cliente.getNickName());
				}
			}
		}

	}

	/**
	 * Comprueba si un cliente está baneado según su nickname.
	 *
	 * @param nickname del cliente
	 * @return true, si esta baneado
	 */
	private boolean testBan(String nickname) {
		return clientesBaneados.containsKey(nickname.toLowerCase());

	}

	/**
	 * Recibe un mensaje como argumento de entrada y comprueba si cumple con la
	 * estructura de un baneo o desbaneo. Si es asi, procede a añadir o elimianr del
	 * mapa el nickname del cliente a banear/desbanear.
	 * 
	 * @param msg mensaje a comprobar
	 * @see ChatMessage
	 */
	private void banClient(ChatMessage msg) {
		// Separamos el el mensaje por espacios
		String[] partes = msg.getMessage().split(" ");

		// Comprobar que el mensaje tiene dos partes: el comando y el nickname
		if (partes.length != 2) return;

		// Convertir a minúsculas para hacer la comparación
		String comando = partes[0].toLowerCase(); // La primera parte es el comando 'ban' o 'unban'
		String nicknameBanned = partes[1].toLowerCase();

		// Un usuario no se puede banear a si mismo
		if (nicknameBanned == msg.getNickname().toLowerCase()) {
			return;
		}

		switch (comando) {
		case "ban":
			// Añadir el nickname al mapa de clientes baneados con el valor true
			clientesBaneados.put(nicknameBanned, true);

			try {
				publish(new ChatMessage(-1, "SERVER", "El cliente " + nicknameBanned + " ha sido baneado."));
			} catch (RemoteException e) {
				System.out.println(e.getMessage());
			}

			System.out.println("[" + getDateString() + "] " + msg.getNickname() + " ha baneado a " + nicknameBanned);
			break;
		case "unban":
			// Si el cliente a desbanear está en la lista de baneados
			if (clientesBaneados.containsKey(nicknameBanned)) {
				// Eliminar el nickname del mapa de clientes baneados para desbanear
				clientesBaneados.remove(nicknameBanned);

				try {
					publish(new ChatMessage(-1, "SERVER", "El cliente " + nicknameBanned + " ha sido desbaneado."));
				} catch (RemoteException e) {
					System.out.println(e.getMessage());
				}

				System.out.println(
						"[" + getDateString() + "] " + msg.getNickname() + " ha desbaneado a " + nicknameBanned);
			} else {
				System.out.println("[" + getDateString() + "] " + "Se intentó desbanear a " + nicknameBanned
						+ " pero no estaba baneado.");
			}
			break;
		}
	}

	/**
	 * Cierra todos los clientes y apaga el servidor.
	 *
	 * @param client cliente
	 * @throws RemoteException the remote exception
	 */
	public void shutdown(ChatClient client) throws RemoteException {
		// Enviar mensaje de cierre a todos los clientes
		for (ChatClient c : clientes.values()) {
			try {
				c.receive(new ChatMessage(-1, "SERVER", "El servidor se está cerrando..."));
			} catch (RemoteException e) {
				System.err.println("[" + getDateString() + "] " + "Error enviando mensaje de cierre al cliente "
						+ c.getNickName());
			}
		}
		// Limpia el mapa de clientes después de enviar el mensaje de cierre
		clientes.clear();
		System.out.println(
				"[" + getDateString() + "] " + client.getNickName() + " ha apagado el servidor.\nAPAGANDO....");
		// El servidor se detendra abruptamente y finaliza la JVM.
		System.exit(0); // Cierre total de JVM
	}

}
