/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package maig;

import java.util.EventListener;

/**
 * @author Daniel Becker
 * @since 18.05.2008
 */
public interface PlayThreadEventListener extends EventListener
{
	public void playThreadEventOccured(PlayThread thread);
}
