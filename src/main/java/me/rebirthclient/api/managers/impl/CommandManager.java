package me.rebirthclient.api.managers.impl;

import com.mojang.realmsclient.gui.ChatFormatting;
import java.util.ArrayList;
import java.util.LinkedList;
import me.rebirthclient.mod.Mod;
import me.rebirthclient.mod.commands.Command;
import me.rebirthclient.mod.commands.impl.BindCommand;
import me.rebirthclient.mod.commands.impl.ClipCommand;
import me.rebirthclient.mod.commands.impl.ConfigCommand;
import me.rebirthclient.mod.commands.impl.CoordsCommand;
import me.rebirthclient.mod.commands.impl.FriendCommand;
import me.rebirthclient.mod.commands.impl.HelpCommand;
import me.rebirthclient.mod.commands.impl.ModuleCommand;
import me.rebirthclient.mod.commands.impl.PeekCommand;
import me.rebirthclient.mod.commands.impl.PrefixCommand;
import me.rebirthclient.mod.commands.impl.ReloadSoundCommand;
import me.rebirthclient.mod.commands.impl.RenameCommand;
import me.rebirthclient.mod.commands.impl.ShrugCommand;
import me.rebirthclient.mod.commands.impl.ToggleCommand;
import me.rebirthclient.mod.commands.impl.UnloadCommand;
import me.rebirthclient.mod.commands.impl.WatermarkCommand;

public class CommandManager extends Mod {
   private final ArrayList<Command> commands = new ArrayList<>();
   private String clientMessage = "[Rebirth]";
   private String prefix = ";";

   public CommandManager() {
      super("Command");
      this.commands.add(new BindCommand());
      this.commands.add(new ModuleCommand());
      this.commands.add(new PrefixCommand());
      this.commands.add(new ConfigCommand());
      this.commands.add(new FriendCommand());
      this.commands.add(new HelpCommand());
      this.commands.add(new UnloadCommand());
      this.commands.add(new ReloadSoundCommand());
      this.commands.add(new CoordsCommand());
      this.commands.add(new ShrugCommand());
      this.commands.add(new WatermarkCommand());
      this.commands.add(new RenameCommand());
      this.commands.add(new ToggleCommand());
      this.commands.add(new PeekCommand());
      this.commands.add(new ClipCommand());
   }

   public static String[] removeElement(String[] input, int indexToDelete) {
      LinkedList<String> result = new LinkedList<>();

      for(int i = 0; i < input.length; ++i) {
         if (i != indexToDelete) {
            result.add(input[i]);
         }
      }

      return result.toArray(input);
   }

   private static String strip(String str) {
      return str.startsWith("\"") && str.endsWith("\"") ? str.substring("\"".length(), str.length() - "\"".length()) : str;
   }

   public void executeCommand(String command) {
      String[] parts = command.split(" (?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
      String name = parts[0].substring(1);
      String[] args = removeElement(parts, 0);

      for(int i = 0; i < args.length; ++i) {
         if (args[i] != null) {
            args[i] = strip(args[i]);
         }
      }

      for(Command c : this.commands) {
         if (c.getName().equalsIgnoreCase(name)) {
            c.execute(parts);
            return;
         }
      }

      Command.sendMessage(ChatFormatting.GRAY + "Command not found, type 'help' for the commands list.");
   }

   public Command getCommandByName(String name) {
      for(Command command : this.commands) {
         if (command.getName().equals(name)) {
            return command;
         }
      }

      return null;
   }

   public ArrayList<Command> getCommands() {
      return this.commands;
   }

   public String getClientMessage() {
      return this.clientMessage;
   }

   public void setClientMessage(String clientMessage) {
      this.clientMessage = clientMessage;
   }

   public String getCommandPrefix() {
      return this.prefix;
   }

   public void setPrefix(String prefix) {
      this.prefix = prefix;
   }
}
