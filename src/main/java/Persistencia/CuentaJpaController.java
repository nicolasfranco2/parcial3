/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Persistencia;

import Logica.Cuenta;
import Persistencia.exceptions.NonexistentEntityException;
import java.io.Serializable;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

/**
 *
 * @author Lenovo
 */
public class CuentaJpaController implements Serializable {

    public CuentaJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    CuentaJpaController() {
        emf = Persistence.createEntityManagerFactory("Parcial3");
    }

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Cuenta cuenta) {
    EntityManager em = null;
    EntityTransaction transaction = null;

  try {
        em = getEntityManager();
        transaction = em.getTransaction();
        transaction.begin();

        // Imprimir información útil antes de persistir
        System.out.println("Antes de persistir: " + cuenta);

        em.persist(cuenta);

        // Imprimir información útil después de persistir
        System.out.println("Después de persistir: " + cuenta);

        transaction.commit();
    } catch (Exception ex) {
        if (transaction != null && transaction.isActive()) {
            transaction.rollback();
        }
        ex.printStackTrace();
        throw new RuntimeException("Error al persistir el cliente", ex);
    } finally {
        if (em != null) {
            em.close();
        }
    }
}



    public void edit(Cuenta cuenta) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            cuenta = em.merge(cuenta);
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                int id = cuenta.getId_cuenta();
                if (findCuenta(id) == null) {
                    throw new NonexistentEntityException("The cuenta with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(int id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Cuenta cuenta;
            try {
                cuenta = em.getReference(Cuenta.class, id);
                cuenta.getId_cuenta();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The cuenta with id " + id + " no longer exists.", enfe);
            }
            em.remove(cuenta);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Cuenta> findCuentaEntities() {
        return findCuentaEntities(true, -1, -1);
    }

    public List<Cuenta> findCuentaEntities(int maxResults, int firstResult) {
        return findCuentaEntities(false, maxResults, firstResult);
    }

    private List<Cuenta> findCuentaEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Cuenta.class));
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

    public Cuenta findCuenta(int id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Cuenta.class, id);
        } finally {
            em.close();
        }
    }

    public int getCuentaCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Cuenta> rt = cq.from(Cuenta.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
