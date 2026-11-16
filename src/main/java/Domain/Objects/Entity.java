package Domain.Objects;

import java.util.Objects;

/**
 * This class is used for domain objects identified by an ID.
 * @param <ID> the type of this entity's identifier
 */
public class Entity<ID> {
    protected ID id;

    /**
     * This function creates an entity with the specified identifier.
     * @param new_id the identifier to assign to the entity;
     */
    public Entity(ID new_id){
        id = new_id;
    }

    /**
     * This function returns this entity's identifier.
     * @return this entity's identifier, which is a generic object;
     */
    public ID getId(){
        return id;
    }

    /**
     * This function replaces this entity's identifier.
     * @param new_id the identifier to assign to the entity;
     */
    public void setId(ID new_id){
        id = new_id;
    }

    /**
     * Returns a hash code based on this entity's identifier.
     * @return the hash code for this entity;
     */
    @Override
    public int hashCode(){
        return Objects.hash(id);
    }

    /**
     * Checks whether the specified object is an entity with an equal identifier.
     * @param new_object the object to compare with;
     * @return {@code true} if the specified object is an entity with an equal identifier;
     *         {@code false} otherwise;
     */
    @Override
    public boolean equals(Object new_object){
        if(new_object instanceof Entity<?> entity){
            return entity.id.equals(this.id);
        }
        return false;
    }
}
