package ngc._resources.actions;

import ngc._resources.actions._config.MergeConfig;
import ngc._resources.models.BaseAction;
import org.powerbot.script.rt4.ClientContext;

public class MergeAction extends BaseAction<ClientContext> {
    private MergeConfig config;

    public MergeAction(ClientContext ctx, String status, MergeConfig _config) {
        super(ctx, status);
        config = _config;
    }

    @Override
    public boolean activate() {
      /*  if(config.isActivateOnFullInventory()){

            return true;
        }else{
            return true;
        }*/

      return true;

    }


    @Override
    public void execute() {
        ctx.movement.running(true);
    }

}
