package celestemoon.idwthp;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.world.GameMode;
import org.lwjgl.glfw.GLFW;

public class IDontWantToHurtPetsClient implements ClientModInitializer {
	public boolean isEnabled = true;

	@Override
	public void onInitializeClient()
	{
		KeyBinding keyToggle = KeyBindingHelper.registerKeyBinding(new KeyBinding(
				"key.idwthp.toggle",
				InputUtil.Type.KEYSYM,
				GLFW.GLFW_KEY_H,
				"category.idwthp.key"
		));

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			while (keyToggle.wasPressed()) {
                assert client.player != null;
				if (this.isEnabled) client.player.sendMessage(Text.translatable("msg.idwthp.disable"), true);
				else client.player.sendMessage(Text.translatable("msg.idwthp.enable"), true);
				this.isEnabled = !this.isEnabled;
			}
		});

		AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
			if (!this.isEnabled) return ActionResult.PASS;
			if (player.getGameMode() == GameMode.SPECTATOR) return ActionResult.PASS;
			if (entity instanceof TameableEntity)
			{
				if (((TameableEntity) entity).isTamed())
				{
					player.sendMessage(Text.translatable("msg.idwthp.attackCanceled"), true);
					return ActionResult.FAIL;
				}
			}
			return ActionResult.PASS;
		});
	}
}