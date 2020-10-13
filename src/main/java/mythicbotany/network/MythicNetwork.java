package mythicbotany.network;

import io.github.noeppi_noeppi.libx.mod.ModX;
import io.github.noeppi_noeppi.libx.network.NetworkX;
import mythicbotany.network.ParticleHandler.ParticleMessage;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.PacketDistributor;

public class MythicNetwork extends NetworkX {

    public MythicNetwork(ModX mod) {
        super(mod);
    }

    @Override
    protected String getProtocolVersion() {
        return "2";
    }

    @Override
    public void registerPackets() {
        register(new ParticleHandler(), NetworkDirection.PLAY_TO_CLIENT);
        register(new InfusionHandler(), NetworkDirection.PLAY_TO_CLIENT);
        register(new PylonHandler(), NetworkDirection.PLAY_TO_CLIENT);

        register(new AlfSwordLeftClickHandler(), NetworkDirection.PLAY_TO_SERVER);
    }

    public void spawnParticle(World world, BasicParticleType particle, int amount, double x, double y, double z, double xm, double ym, double zm, double xd, double yd, double zd) {
        spawnParticle(world, particle, amount, x, y, z, xm, ym, zm, xd, yd, zd, false);
    }

    public void spawnParticle(World world, BasicParticleType particle, int amount, double x, double y, double z, double xm, double ym, double zm, double xd, double yd, double zd, boolean randomizePosition) {
        if (world.isRemote) {
            for (int i = 0; i < amount; i++) {
                if (randomizePosition) {
                    world.addParticle(particle,
                            x + (world.rand.nextDouble() * 2 * xd) - xd,
                            y + (world.rand.nextDouble() * 2 * yd) - yd,
                            z + (world.rand.nextDouble() * 2 * zd) - zd,
                            xm, ym, zm);
                } else {
                    world.addParticle(particle, x, y, z,
                            xm + (world.rand.nextDouble() * 2 * xd) - xd,
                            ym + (world.rand.nextDouble() * 2 * yd) - yd,
                            zm + (world.rand.nextDouble() * 2 * zd) - zd);
                }
            }
        } else {
            instance.send(PacketDistributor.NEAR.with(() -> new PacketDistributor.TargetPoint(x, y, z, 100, world.func_234923_W_())),
                    new ParticleMessage(particle.getRegistryName(), world.func_234923_W_().getRegistryName(), x, y, z, amount, xm, ym, zm, xd, yd, zd, randomizePosition));
        }
    }

    public void spawnInfusionParticles(World world, BlockPos pos, double progress, int fromColor, int toColor) {
        if (!world.isRemote) {
           instance.send(PacketDistributor.TRACKING_CHUNK.with(() -> world.getChunkAt(pos)), new InfusionHandler.InfusionMessage(pos.getX(), pos.getY(), pos.getZ(), world.func_234923_W_().getRegistryName(), progress, fromColor, toColor));
        }
    }
}
