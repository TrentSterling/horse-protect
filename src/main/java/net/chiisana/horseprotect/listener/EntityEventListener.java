package net.chiisana.horseprotect.listener;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.*;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class EntityEventListener implements Listener
{
	// TODO: SEARCH AND REPLACE WOLF -> HORSE; Wolf -> Horse

	@EventHandler(priority = EventPriority.NORMAL)
	public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent event)
	{
		if (event.getEntity().getType() != EntityType.WOLF)
		{
			// Break out if we're not dealing with a horse
			return;
		}
		Wolf horse = (Wolf)event.getEntity();
		if (!horse.isTamed()) {
			// Break out if we're not dealing with a tamed horse
			return;
		}

		Player owner;
		if (horse.getOwner() instanceof Player)
		{
			// Simple case, we have an owner
			owner = (Player)horse.getOwner();
		} else if (horse.getOwner() instanceof OfflinePlayer) {
			// More tricky, for now, we'll just try to get a Player, if we can't get it, we can't guard it (yet)
			if (((OfflinePlayer) horse.getOwner()).getPlayer() != null)
			{
				owner = ((OfflinePlayer) horse.getOwner()).getPlayer();
			} else {
				return;
			}

		} else {
			// Human entity, NPC owned, we don't care at this point
			return;
		}

		if (!((event.getDamager() instanceof Player) || (event.getDamager() instanceof Projectile)))
		{
			// Break out if we're not dealing with a player damage source or projectile damage source, no invincible horses!
			return;
		}


		Player attacker;
		if (event.getDamager() instanceof Player)
		{
			attacker = (Player)event.getDamager();
			_helperCheckPermission(event, owner, horse, attacker);
			return;
		}

		if (event.getDamager() instanceof Projectile)
		{
			Projectile projectile = (Projectile)event.getDamager();
			if (projectile.getShooter() == null) {
				// Came from a dispenser, we should check whether or not to allow this
				_helperCheckPermission(event, owner, horse, null);
				return;
			}

			if (!(projectile.getShooter() instanceof Player))
			{
				// Other mobs shot a projectile at it
				return;
			}

			attacker = (Player) projectile.getShooter();
			_helperCheckPermission(event, owner, horse, attacker);
			return;
		}

	}

	private void _helperCheckPermission(EntityDamageByEntityEvent event, Player owner, Wolf horse, Player attacker)
	{
		if (attacker.getName().equals(owner.getName()))
		{
			// Owner killing own horse, allow it
			return;
		} else {
			// Another player attacking horse
			if (owner.hasPermission("horseprotect.protecthorses"))
			{
				event.setCancelled(true);
			}

			if (owner.hasPermission("horseprotect.reflectdamages") && (attacker != null))
			{
				// Reflect the damage
				int damage = event.getDamage();
				attacker.damage(damage, horse);
			}
			return;
		}
	}
}
