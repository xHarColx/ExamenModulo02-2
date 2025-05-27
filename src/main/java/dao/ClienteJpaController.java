package dao;

import dao.exceptions.NonexistentEntityException;
import dto.Cliente;
import java.io.Serializable;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.Persistence;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import util.DESCipher;
import util.SHA256Util;

/**
 *
 * @author harol
 */
public class ClienteJpaController implements Serializable {

    public ClienteJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("com.mycompany_Fact_war_1.0-SNAPSHOTPU");

   

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public ClienteJpaController() {
    }

    public void create(Cliente cliente) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            em.persist(cliente);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Cliente cliente) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            cliente = em.merge(cliente);
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = cliente.getCodiClie();
                if (findCliente(id) == null) {
                    throw new NonexistentEntityException("The cliente with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Cliente cliente;
            try {
                cliente = em.getReference(Cliente.class, id);
                cliente.getCodiClie();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The cliente with id " + id + " no longer exists.", enfe);
            }
            em.remove(cliente);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Cliente> findClienteEntities() {
        return findClienteEntities(true, -1, -1);
    }

    public List<Cliente> findClienteEntities(int maxResults, int firstResult) {
        return findClienteEntities(false, maxResults, firstResult);
    }

    private List<Cliente> findClienteEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Cliente.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public Cliente findCliente(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Cliente.class, id);
        } finally {
            em.close();
        }
    }

    public int getClienteCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Cliente> rt = cq.from(Cliente.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }

    public Cliente validarUsuario(Cliente u) {
        EntityManager em = getEntityManager();
        try {
            Query q = em.createNamedQuery("Cliente.validar", Cliente.class);
            q.setParameter("logiClie", u.getLogiClie());
            q.setParameter("passClie", u.getPassClie());
            return (Cliente) q.getSingleResult();
        } catch (Exception ex) {
            String mensaje = ex.getMessage();
            return null;
        } finally {
            em.close();
        }
    }

    public String cambiarClave(Cliente u, String nuevaClave) {
        EntityManager em = getEntityManager();
        try {
            Cliente usuario = validarUsuario(u);
            if (usuario != null) { // Verifica que el usuario exista
                if (usuario.getPassClie().equals(u.getPassClie())) {
                    usuario.setPassClie(nuevaClave);
                    edit(usuario);
                    return "Clave cambiada";
                } else {
                    return "Clave actual no válida";
                }
            } else {
                return "Usuario no encontrado"; // Manejo de usuario no encontrado
            }
        } catch (Exception ex) {
            return null;
        } finally {
            em.close();
        }
    }

    public static void main(String[] args) throws Exception {
        ClienteJpaController vurDAO = new ClienteJpaController();
        Cliente vur = vurDAO.validarUsuario(new Cliente("harol", "03ac674216f3e15c761ee1a5e255f067953623c8b388b4459e13f978d7c846f4"));
        String clave = "12345678";
        String pass = "1234";
        String contraCifrada = DESCipher.cifrar(pass, clave);
        System.out.println("Contra cifrada: " + contraCifrada);
        System.out.println("---------------------------");
        String contraDescifrada = DESCipher.descifrar(contraCifrada, clave);
        System.out.println("Contra descifrada: " + contraDescifrada);
        System.out.println("---------------------------");
        String contraSHA512 = SHA256Util.hash(contraDescifrada);
        System.out.println("Contra usando SHA256: " + contraSHA512);

        if (vur != null) {
            System.out.println("PERSONA ENCONTRADA");
        } else {
            System.out.println("PERSONA NO ENCONTRADA");
        }
    }

}
