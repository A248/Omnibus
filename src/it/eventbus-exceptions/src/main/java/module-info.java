import space.arim.omnibus.eventbusexceptions.ToggleableLoggerFinder;

module space.arim.omnibus.eventbusexceptions {
	requires space.arim.omnibus;
	requires java.logging;
	provides System.LoggerFinder with ToggleableLoggerFinder;
}